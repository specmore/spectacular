package spectacular.backend.catalogues;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import spectacular.backend.cataloguemanifest.CatalogueManifestParser;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.RestApiClient;
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
   * Checks if a Spec File belongs to the specified Catalogue for a given user.
   *
   * @param catalogueRepo the repository of the catalogue to check
   * @param username the user who's access needs to be checked
   * @param specRepo the repository where the spec file can be found
   * @param specFilePath the filepath of the spec file
   * @return true if the spec file specified is listed in the specified catalogue and the user has access to the catalogue
   */
  public boolean isSpecFileInCatalogue(RepositoryId catalogueRepo, String username, RepositoryId specRepo, String specFilePath) {
    //    var catalogue = getCatalogueForRepoAndUser(catalogueRepo, username);
    //
    //    if (catalogue == null || catalogue.getCatalogueManifest() == null ||
    //    catalogue.getCatalogueManifest().getSpecFileLocations() == null) {
    //      return false;
    //    }
    //
    //    return catalogue.getCatalogueManifest().getSpecFileLocations().stream()
    //        .anyMatch(specFileLocation -> specFileMatches(specFileLocation, catalogueRepo, specRepo, specFilePath));
    return false;
  }

  //  private boolean specFileMatches(SpecFileLocation specFileLocation, Repository catalogueRepo, Repository specRepo,
  //  String specFilePath) {
  //    if (specFileLocation.getRepo() == null && !catalogueRepo.equals(specRepo)) {
  //      return false;
  //    }
  //    if (specFileLocation.getRepo() != null && !specFileLocation.getRepo().equals(specRepo)) {
  //      return false;
  //    }
  //    return specFileLocation.getFilePath().equalsIgnoreCase(specFilePath);
  //  }

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
        return CatalogueMapper.mapCatalogueManifestEntries(catalogueManifestParseResult.getCatalogueManifest(), manifestId);
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
    spectacular.backend.api.model.Catalogue catalogue = null;
    List<spectacular.backend.api.model.SpecLog> specLogs = null;
    var fileContentItem = restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), catalogueId.getPath(), null);
    try {
      var catalogueParseResult = CatalogueManifestParser.findAndParseCatalogueInManifestFileContents(
          fileContentItem.getDecodedContent(),
          catalogueId.getCatalogueName());

      if (catalogueParseResult.getCatalogue() != null) {
        catalogue = CatalogueMapper.mapCatalogue(catalogueParseResult.getCatalogue(), catalogueId);
        specLogs = specLogService.getSpecLogsFor(catalogueParseResult.getCatalogue(), catalogueId);
      } else {
        return CatalogueMapper.createForParseError(catalogueParseResult.getError(), catalogueId);
      }
    } catch (UnsupportedEncodingException e) {
      logger.error("An error occurred while decoding the catalogue manifest yaml file: " +
          ((CatalogueManifestId)catalogueId).toString(), e);
      var error = "An error occurred while decoding the catalogue manifest yaml file: " + e.getMessage();
      return CatalogueMapper.createForParseError(error, catalogueId);
    }

    return catalogue.specLogs(specLogs);
  }
}
