package spectacular.backend.catalogues;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import spectacular.backend.cataloguemanifest.CatalogueManifestParser;
import spectacular.backend.cataloguemanifest.CatalogueParseResult;
import spectacular.backend.cataloguemanifest.model.Interface;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.common.CatalogueManifestId;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.domain.ContentItem;
import spectacular.backend.github.domain.SearchCodeResultItem;
import spectacular.backend.specs.SpecLogService;

@Service
public class CatalogueService {
  private static final String CATALOGUE_MANIFEST_FILE_NAME = "spectacular-config";
  private static final String CATALOGUE_MANIFEST_YAML_FILE_EXTENSION = "yaml";
  private static final String CATALOGUE_MANIFEST_YML_FILE_EXTENSION = "yml";
  private static final String CATALOGUE_MANIFEST_FILE_PATH = "/";
  private static final String CATALOGUE_MANIFEST_FULL_YAML_FILE_NAME =
      CATALOGUE_MANIFEST_FILE_NAME + "." + CATALOGUE_MANIFEST_YAML_FILE_EXTENSION;
  private static final String CATALOGUE_MANIFEST_FULL_YML_FILE_NAME =
      CATALOGUE_MANIFEST_FILE_NAME + "." + CATALOGUE_MANIFEST_YML_FILE_EXTENSION;

  private static final Logger logger = LoggerFactory.getLogger(CatalogueService.class);

  private final RestApiClient restApiClient;
  private final SpecLogService specLogService;

  /**
   * A service component that encapsulates all the logic required to build Catalogue objects from the information stored in git repositories
   * accessible for a given request's installation context.
   *
   * @param restApiClient an API client to retrieve information about the git repositories
   * @param specLogService a service component providing the functionality to retrieve Spec Log items referenced within the catalogue
   *     files
   */
  public CatalogueService(RestApiClient restApiClient,
                          SpecLogService specLogService) {
    this.restApiClient = restApiClient;
    this.specLogService = specLogService;
  }

  /**
   * Find all catalogues a given user can access in a specific organisation.
   *
   * @param orgName the name of the organisation
   * @param username the username of the user
   * @return a list of all the accessible catalogues
   */
  public List<spectacular.backend.api.model.Catalogue> findCataloguesForOrgAndUser(String orgName, String username) {
    var catalogues = findCataloguesForOrg(orgName).stream()
        .filter(catalogueManifestId -> isRepositoryAccessible(catalogueManifestId.getRepositoryId(), username))
        .map(this::getCataloguesFromManifest)
        .flatMap(cataloguesList -> cataloguesList.stream())
        .collect(Collectors.toList());
    return catalogues;
  }

  /**
   * Get a Catalogue matching the given identifier and accessible for the given user.
   *
   * @param catalogueId the identifier giving the exact location of the catalogue definition
   * @param username the username of the user
   * @return A Catalogue object
   */
  public spectacular.backend.api.model.Catalogue getCatalogueForUser(CatalogueId catalogueId, String username) {
    if (!isRepositoryAccessible(catalogueId.getRepositoryId(), username)) {
      return null;
    }

    return getFullCatalogueDetails(catalogueId);
  }

  /**
   * Gets a specific interface entry for a given interface name in the catalogue manifest that a given user has access to.
   *
   * @param catalogueId the composite identifier of the catalogue to find the interface in
   * @param interfaceName the identifier of interface to retrieve
   * @param username the user who's access needs to be checked
   * @return an interface object representing the interface entry in the specified catalogue manifest
   *     or null if the Catalogue or Interface could not be found or the user does not have access to the catalogue
   */
  public Interface getInterfaceEntry(CatalogueId catalogueId, String interfaceName, String username) throws UnsupportedEncodingException {
    if (!isRepositoryAccessible(catalogueId.getRepositoryId(), username)) {
      return null;
    }

    var getAndParseCatalogueResult = getAndParseCatalogueInManifest(catalogueId);

    if (getAndParseCatalogueResult.getCatalogueManifestFileContentItem() == null) {
      return null;
    }

    var parseError = getAndParseCatalogueResult.getCatalogueParseResult().getError();
    if (parseError != null) {
      logger.warn("A request for an interface entry in a catalogue manifest that does not parse correctly was received. " +
          "CatalogueId: {} and ParseError '{}'", catalogueId, parseError);
      throw new RuntimeException("An error occurred while parsing the catalogue manifest file for interface requested.");
    }

    var catalogue = getAndParseCatalogueResult.getCatalogueParseResult().getCatalogue();

    return catalogue.getInterfaces().getAdditionalProperties().get(interfaceName);
  }

  private CatalogueManifestId pickCatalogueFileFromSearchResults(List<SearchCodeResultItem> searchCodeResultItems) {
    var manifestFileResult = searchCodeResultItems.stream()
        .filter(resultItem -> isExactFileNameMatch(resultItem, CATALOGUE_MANIFEST_FULL_YML_FILE_NAME))
        .findFirst();

    if (manifestFileResult.isPresent()) {
      return CatalogueManifestId.createFrom(manifestFileResult.get());
    }

    manifestFileResult = searchCodeResultItems.stream()
        .filter(resultItem -> isExactFileNameMatch(resultItem, CATALOGUE_MANIFEST_FULL_YAML_FILE_NAME))
        .findFirst();

    if (manifestFileResult.isPresent()) {
      return CatalogueManifestId.createFrom(manifestFileResult.get());
    }

    return null;
  }

  private List<CatalogueManifestId> findCataloguesForOrg(String orgName) {
    var searchCodeResults = restApiClient.findFiles(CATALOGUE_MANIFEST_FILE_NAME,
        List.of(CATALOGUE_MANIFEST_YAML_FILE_EXTENSION, CATALOGUE_MANIFEST_YML_FILE_EXTENSION),
        CATALOGUE_MANIFEST_FILE_PATH,
        orgName, null);
    logger.debug("find catalogue manifest files results for org '" + orgName + "': " + searchCodeResults.toString());

    var repositorySearchCodeResultsMap = searchCodeResults.getItems().stream()
        .filter(resultItem -> isExactFileNameMatch(resultItem, CATALOGUE_MANIFEST_FULL_YAML_FILE_NAME) ||
            isExactFileNameMatch(resultItem, CATALOGUE_MANIFEST_FULL_YML_FILE_NAME))
        .collect(Collectors.groupingBy(RepositoryId::createRepositoryFrom));

    return repositorySearchCodeResultsMap.entrySet().stream()
        .map(entry -> pickCatalogueFileFromSearchResults(entry.getValue()))
        .collect(Collectors.toList());
  }

  private boolean isExactFileNameMatch(SearchCodeResultItem searchCodeResultItem, String filename) {
    return searchCodeResultItem.getName().equals(filename);
  }

  private boolean isRepositoryAccessible(RepositoryId repo, String username) {
    try {
      return restApiClient.isUserRepositoryCollaborator(repo, username);
    } catch (HttpClientErrorException ex) {
      logger.debug("An error occurred while trying to check collaborators for repo: " + repo.getNameWithOwner(), ex);
      return false;
    }
  }

  private List<spectacular.backend.api.model.Catalogue> getCataloguesFromManifest(CatalogueManifestId manifestId) {
    var fileContentItem = restApiClient.getRepositoryContent(manifestId.getRepositoryId(), manifestId.getPath(), null);
    try {
      var catalogueManifestParseResult = CatalogueManifestParser.parseManifestFileContents(fileContentItem.getDecodedContent());

      if (catalogueManifestParseResult.getCatalogueManifest() != null) {
        return CatalogueMapper.mapCatalogueManifestEntries(
            catalogueManifestParseResult.getCatalogueManifest(),
            manifestId,
            fileContentItem.getHtml_url());
      } else {
        return Collections.singletonList(CatalogueMapper.createForParseError(catalogueManifestParseResult.getError(), manifestId));
      }
    } catch (UnsupportedEncodingException e) {
      logger.error("An error occurred while decoding the catalogue manifest yaml file: " + manifestId.toString(), e);
      var error = "An error occurred while decoding the catalogue manifest yaml file: " + e.getMessage();
      return Collections.singletonList(CatalogueMapper.createForParseError(error, manifestId));
    }
  }

  private spectacular.backend.api.model.Catalogue getFullCatalogueDetails(CatalogueId catalogueId) {
    GetAndParseCatalogueResult getAndParseCatalogueResult = null;
    try {
      getAndParseCatalogueResult = getAndParseCatalogueInManifest(catalogueId);
    } catch (UnsupportedEncodingException e) {
      logger.error("An error occurred while decoding the catalogue manifest yml file: " + ((CatalogueManifestId)catalogueId).toString(), e);
      var error = "An error occurred while decoding the catalogue manifest yml file: " + e.getMessage();
      return CatalogueMapper.createForParseError(error, catalogueId);
    }

    if (getAndParseCatalogueResult.getCatalogueManifestFileContentItem() == null) {
      return null;
    }

    var parseError = getAndParseCatalogueResult.getCatalogueParseResult().getError();
    if (parseError != null) {
      return CatalogueMapper.createForParseError(parseError, catalogueId);
    }

    var catalogue = getAndParseCatalogueResult.getCatalogueParseResult().getCatalogue();
    var catalogueManifestFileContentItem = getAndParseCatalogueResult.getCatalogueManifestFileContentItem();
    var catalogueDetails = CatalogueMapper.mapCatalogue(catalogue, catalogueId, catalogueManifestFileContentItem.getHtml_url());
    var specLogs = specLogService.getSpecLogsFor(catalogue, catalogueId);
    return catalogueDetails.specLogs(specLogs);
  }

  private GetAndParseCatalogueResult getAndParseCatalogueInManifest(CatalogueId catalogueId) throws UnsupportedEncodingException {
    ContentItem fileContentItem = null;

    try {
      fileContentItem = restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), catalogueId.getPath(), null);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.warn("A request for a catalogue manifest that does not exist was received. CatalogueId: {}", catalogueId);
        return new GetAndParseCatalogueResult(null, null);
      }
    }

    String fileContents = fileContentItem.getDecodedContent();
    var catalogueParseResult = CatalogueManifestParser.findAndParseCatalogueInManifestFileContents(fileContents,
        catalogueId.getCatalogueName());

    return new GetAndParseCatalogueResult(fileContentItem, catalogueParseResult);
  }

  private class GetAndParseCatalogueResult {
    private final ContentItem catalogueManifestFileContentItem;
    private final CatalogueParseResult catalogueParseResult;

    private GetAndParseCatalogueResult(ContentItem catalogueManifestFileContentItem,
                                       CatalogueParseResult catalogueParseResult) {
      this.catalogueManifestFileContentItem = catalogueManifestFileContentItem;
      this.catalogueParseResult = catalogueParseResult;
    }

    public ContentItem getCatalogueManifestFileContentItem() {
      return catalogueManifestFileContentItem;
    }

    public CatalogueParseResult getCatalogueParseResult() {
      return catalogueParseResult;
    }
  }
}
