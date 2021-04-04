package spectacular.backend.catalogues;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import spectacular.backend.api.model.GetInterfaceResult;
import spectacular.backend.cataloguemanifest.CatalogueManifestParser;
import spectacular.backend.cataloguemanifest.CatalogueManifestProvider;
import spectacular.backend.cataloguemanifest.GetAndParseCatalogueResult;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.common.CatalogueManifestId;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.domain.SearchCodeResultItem;
import spectacular.backend.interfaces.InterfaceFileContents;
import spectacular.backend.interfaces.InterfaceService;

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
  private final CatalogueManifestParser catalogueManifestParser;
  private final CatalogueManifestProvider catalogueManifestProvider;
  private final CatalogueMapper catalogueMapper;
  private final InterfaceService interfaceService;

  /**
   * A service component that encapsulates all the logic required to build Catalogue objects from the information stored in git repositories
   * accessible for a given request's installation context.
   * @param restApiClient an API client to retrieve information about the git repositories
   * @param catalogueManifestParser a helper service to parse catalogue manifest file content into concrete objects
   * @param catalogueManifestProvider a data provider that retrieves catalogue manifest files from the data source
   * @param catalogueMapper a helper service for mapping catalogue manifest objects to API model objects
   * @param interfaceService a service for retrieving more interface information for an interface configured in a catalogue manifest
   */
  public CatalogueService(RestApiClient restApiClient,
                          CatalogueManifestParser catalogueManifestParser,
                          CatalogueManifestProvider catalogueManifestProvider,
                          CatalogueMapper catalogueMapper,
                          InterfaceService interfaceService) {
    this.restApiClient = restApiClient;
    this.catalogueManifestParser = catalogueManifestParser;
    this.catalogueManifestProvider = catalogueManifestProvider;
    this.catalogueMapper = catalogueMapper;
    this.interfaceService = interfaceService;
  }

  /**
   * Find all catalogues a given user can access in a specific organisation.
   *
   * @param orgName the name of the organisation
   * @param username the username of the user
   * @return a list of all the accessible catalogues
   */
  public List<spectacular.backend.api.model.Catalogue> findCataloguesForOrgAndUser(String orgName, String username) {
    return findCataloguesForOrg(orgName).stream()
        .filter(catalogueManifestId -> isRepositoryAccessible(catalogueManifestId.getRepositoryId(), username))
        .map(this::getCataloguesFromManifest)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  /**
   * Get a Catalogue matching the given identifier and accessible for the given user.
   *
   * @param catalogueId the identifier giving the exact location of the catalogue definition
   * @param username the username of the user
   * @return A Catalogue object
   */
  public GetCatalogueForUserResult getCatalogueForUser(CatalogueId catalogueId, String username) {
    GetAndParseCatalogueResult getAndParseCatalogueResult = catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, username);

    if (!getAndParseCatalogueResult.isCatalogueManifestFileExists()) {
      return GetCatalogueForUserResult.createNotFoundResult("Catalogue manifest file not found: " + catalogueId.getFullPath());
    }

    var catalogueEntryParseResult = getAndParseCatalogueResult.getCatalogueParseResult();

    if (catalogueEntryParseResult.isCatalogueEntryNotFound()) {
      return GetCatalogueForUserResult.createNotFoundResult("Catalogue entry in manifest file not found: " + catalogueId.getCombined());
    }

    if (catalogueEntryParseResult.isCatalogueEntryContainsError()) {
      var catalogueDetails = catalogueMapper.createForParseError(catalogueEntryParseResult.getError(), catalogueId);
      return GetCatalogueForUserResult.createFoundResult(catalogueDetails);
    }

    var catalogueEntry = catalogueEntryParseResult.getCatalogue();
    var manifestUrl = getAndParseCatalogueResult.getCatalogueManifestFileHtmlUrl();
    var catalogueDetails = catalogueMapper.mapCatalogue(catalogueEntry, catalogueId, manifestUrl);

    var specEvolutionSummaries = catalogueEntry.getInterfaces().getAdditionalProperties().entrySet().stream()
        .map(interfaceEntry -> {
          var getInterfaceResult = this.interfaceService.getInterfaceDetails(catalogueId, interfaceEntry.getValue(), interfaceEntry.getKey());
          return getInterfaceResult.getSpecEvolutionSummary();
        })
        .collect(Collectors.toList());

    var catalogueDetailsWithInterfaceSummaries = catalogueDetails.specEvolutionSummaries(specEvolutionSummaries);

    return GetCatalogueForUserResult.createFoundResult(catalogueDetailsWithInterfaceSummaries);
  }

  public GetInterfaceDetailsResult getInterfaceDetails(CatalogueId catalogueId, String interfaceName, String username) {
    GetAndParseCatalogueResult getAndParseCatalogueResult = catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, username);

    if (!getAndParseCatalogueResult.isCatalogueManifestFileExists()) {
      return GetInterfaceDetailsResult.createNotFoundResult("Catalogue manifest file not found: " + catalogueId.getFullPath());
    }

    var catalogueEntryParseResult = getAndParseCatalogueResult.getCatalogueParseResult();

    if (catalogueEntryParseResult.isCatalogueEntryNotFound()) {
      return GetInterfaceDetailsResult.createNotFoundResult("Catalogue entry in manifest file not found: " + catalogueId.getCombined());
    }

    if (catalogueEntryParseResult.isCatalogueEntryContainsError()) {
      return GetInterfaceDetailsResult.createConfigErrorResult("Catalogue entry in manifest file: " + catalogueId.getCombined() +
          ", has parse error: " + catalogueEntryParseResult.getError());
    }

    var catalogueEntry = catalogueEntryParseResult.getCatalogue();
    if (!catalogueEntry.getInterfaces().getAdditionalProperties().containsKey(interfaceName)) {
      return GetInterfaceDetailsResult.createNotFoundResult("Interface entry not found in Catalogue entry in manifest file: " + catalogueId.getCombined() +
          ", with name: " + interfaceName);
    }

    var catalogueInterfaceEntry = catalogueEntry.getInterfaces().getAdditionalProperties().get(interfaceName);
    var interfaceDetailsResult = this.interfaceService.getInterfaceDetails(catalogueId, catalogueInterfaceEntry, interfaceName);

    if (interfaceDetailsResult == null) {
      return GetInterfaceDetailsResult.createConfigErrorResult("Interface entry in Catalogue entry in manifest file: " + catalogueId.getCombined() +
          ", with name: " + interfaceName + ", has no spec file location set.");
    }

    var manifestUrl = getAndParseCatalogueResult.getCatalogueManifestFileHtmlUrl();
    var catalogueDetails = catalogueMapper.mapCatalogue(catalogueEntry, catalogueId, manifestUrl);
    var interfaceDetails = interfaceDetailsResult.catalogue(catalogueDetails);
    return GetInterfaceDetailsResult.createFoundResult(interfaceDetails);
  }

  public InterfaceFileContents getInterfaceFileContents(CatalogueId catalogueId, String interfaceName, String ref, String username)
      throws UnsupportedEncodingException {
    GetAndParseCatalogueResult getAndParseCatalogueResult = catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, username);

    if (!getAndParseCatalogueResult.isCatalogueManifestFileExists()) {
      return null;
    }

    var parseError = getAndParseCatalogueResult.getCatalogueParseResult().getError();
    if (parseError != null) {
      logger.warn("A request for an interface entry in a catalogue manifest that does not parse correctly was received. " +
          "CatalogueId: {} and ParseError '{}'", catalogueId, parseError);
      throw new RuntimeException("An error occurred while parsing the catalogue manifest file for interface requested.");
    }

    var catalogue = getAndParseCatalogueResult.getCatalogueParseResult().getCatalogue();

    if (catalogue == null) {
      return null;
    }

    var catalogueInterfaceEntry = catalogue.getInterfaces().getAdditionalProperties().get(interfaceName);

    if (catalogueInterfaceEntry == null) {
      return null;
    }

    return this.interfaceService.getInterfaceFileContents(catalogueId, catalogueInterfaceEntry, ref);
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

    return manifestFileResult.map(CatalogueManifestId::createFrom).orElse(null);

  }

  private List<CatalogueManifestId> findCataloguesForOrg(String orgName) {
    var searchCodeResults = restApiClient.findFiles(CATALOGUE_MANIFEST_FILE_NAME,
        List.of(CATALOGUE_MANIFEST_YAML_FILE_EXTENSION, CATALOGUE_MANIFEST_YML_FILE_EXTENSION),
        CATALOGUE_MANIFEST_FILE_PATH,
        orgName, null);
    logger.debug("find catalogue manifest files results for org '{}': {}", orgName, searchCodeResults.toString());

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
      var catalogueManifestParseResult = catalogueManifestParser.parseManifestFileContents(fileContentItem.getDecodedContent());

      if (catalogueManifestParseResult.getCatalogueManifest() != null) {
        return catalogueMapper.mapCatalogueManifestEntries(
            catalogueManifestParseResult.getCatalogueManifest(),
            manifestId,
            fileContentItem.getHtml_url());
      } else {
        return Collections.singletonList(catalogueMapper.createForParseError(catalogueManifestParseResult.getError(), manifestId));
      }
    } catch (UnsupportedEncodingException e) {
      logger.error("An error occurred while decoding the catalogue manifest yaml file: " + manifestId.toString(), e);
      var error = "An error occurred while decoding the catalogue manifest yaml file: " + e.getMessage();
      return Collections.singletonList(catalogueMapper.createForParseError(error, manifestId));
    }
  }
}
