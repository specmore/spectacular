package spectacular.backend.cataloguemanifest;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.common.CatalogueManifestId;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.domain.ContentItem;
import spectacular.backend.github.domain.SearchCodeResultItem;

@Service
public class CatalogueManifestProvider {
  private static final String CATALOGUE_MANIFEST_FILE_NAME = "spectacular-config";
  private static final String CATALOGUE_MANIFEST_YAML_FILE_EXTENSION = "yaml";
  private static final String CATALOGUE_MANIFEST_YML_FILE_EXTENSION = "yml";
  private static final String CATALOGUE_MANIFEST_FILE_PATH = "/";
  private static final String CATALOGUE_MANIFEST_FULL_YAML_FILE_NAME =
      CATALOGUE_MANIFEST_FILE_NAME + "." + CATALOGUE_MANIFEST_YAML_FILE_EXTENSION;
  private static final String CATALOGUE_MANIFEST_FULL_YML_FILE_NAME =
      CATALOGUE_MANIFEST_FILE_NAME + "." + CATALOGUE_MANIFEST_YML_FILE_EXTENSION;
  private static final Logger logger = LoggerFactory.getLogger(CatalogueManifestProvider.class);

  private final RestApiClient restApiClient;

  public CatalogueManifestProvider(RestApiClient restApiClient) {
    this.restApiClient = restApiClient;
  }

  /**
   * Gets an interface catalogue item from a catalogue manifest file.
   *
   * @param catalogueManifestId an identifier object containing the location of the manifest file
   * @param username the username of the user trying to access the catalogue
   * @return a GetCatalogueManifestFileContentResult object
   */
  public GetCatalogueManifestFileContentResult getCatalogueManifest(CatalogueManifestId catalogueManifestId, String username) {
    if (!isRepositoryAccessible(catalogueManifestId.getRepositoryId(), username)) {
      return GetCatalogueManifestFileContentResult.createNotFoundResult(catalogueManifestId);
    }

    return getCatalogueManifestFileContent(catalogueManifestId);
  }

  /**
   * Searches for catalogue manifest files that are in repositories that belong to an organisation and the user has access to.
   *
   * @param orgName the name of the organisation to search repositories in
   * @param username the user who's access to check
   * @return all the manifest files that are found and their file content
   */
  public List<GetCatalogueManifestFileContentResult> findCatalogueManifestsForOrg(String orgName, String username) {
    var searchCodeResults = restApiClient.findFiles(CATALOGUE_MANIFEST_FILE_NAME,
        List.of(CATALOGUE_MANIFEST_YAML_FILE_EXTENSION, CATALOGUE_MANIFEST_YML_FILE_EXTENSION),
        CATALOGUE_MANIFEST_FILE_PATH,
        orgName, null);
    logger.debug("find catalogue manifest files results for org '{}': {}", orgName, searchCodeResults.toString());

    var repositorySearchCodeResultsMap = searchCodeResults.getItems().stream()
        .filter(resultItem -> isExactFileNameMatch(resultItem, CATALOGUE_MANIFEST_FULL_YAML_FILE_NAME) ||
            isExactFileNameMatch(resultItem, CATALOGUE_MANIFEST_FULL_YML_FILE_NAME))
        .collect(Collectors.groupingBy(RepositoryId::createRepositoryFrom));

    return repositorySearchCodeResultsMap.values().stream()
        .map(this::pickCatalogueFileFromSearchResults)
        .filter(catalogueManifestId -> isRepositoryAccessible(catalogueManifestId.getRepositoryId(), username))
        .map(this::getCatalogueManifestFileContent)
        .collect(Collectors.toList());
  }

  private GetCatalogueManifestFileContentResult getCatalogueManifestFileContent(CatalogueManifestId catalogueManifestId) {
    ContentItem fileContentItem = null;

    try {
      fileContentItem = restApiClient.getRepositoryContent(catalogueManifestId.getRepositoryId(), catalogueManifestId.getPath(), null);
      return GetCatalogueManifestFileContentResult.createSuccessfulResult(catalogueManifestId, fileContentItem);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.warn("A request for a catalogue manifest that does not exist was received. catalogueManifestId: {}", catalogueManifestId);
        return GetCatalogueManifestFileContentResult.createNotFoundResult(catalogueManifestId);
      } else {
        throw e;
      }
    }
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
}
