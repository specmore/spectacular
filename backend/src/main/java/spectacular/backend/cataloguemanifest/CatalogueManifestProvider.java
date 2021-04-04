package spectacular.backend.cataloguemanifest;

import java.io.UnsupportedEncodingException;
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

@Service
public class CatalogueManifestProvider {
  private static final Logger logger = LoggerFactory.getLogger(CatalogueManifestProvider.class);

  private final RestApiClient restApiClient;
  private final CatalogueManifestParser catalogueManifestParser;

  public CatalogueManifestProvider(RestApiClient restApiClient,
                                   CatalogueManifestParser catalogueManifestParser) {
    this.restApiClient = restApiClient;
    this.catalogueManifestParser = catalogueManifestParser;
  }

  public GetAndParseCatalogueResult getAndParseCatalogueInManifest(CatalogueId catalogueId, String username) {
    if (!isRepositoryAccessible(catalogueId.getRepositoryId(), username)) {
      return GetAndParseCatalogueResult.createFileNotFoundResult();
    }

    ContentItem fileContentItem = null;

    try {
      fileContentItem = restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), catalogueId.getPath(), null);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.warn("A request for a catalogue manifest that does not exist was received. CatalogueId: {}", catalogueId);
        return GetAndParseCatalogueResult.createFileNotFoundResult();
      } else {
        throw e;
      }
    }

    String fileContents = null;
    FindAndParseCatalogueResult catalogueParseResult = null;
    try {
      fileContents = fileContentItem.getDecodedContent();
      catalogueParseResult = catalogueManifestParser.findAndParseCatalogueInManifestFileContents(fileContents,
          catalogueId.getCatalogueName());
    } catch (UnsupportedEncodingException e) {
      logger.error("An error occurred while decoding the catalogue manifest yml file: " + ((CatalogueManifestId)catalogueId).toString(), e);
      var error = "An error occurred while decoding the catalogue manifest yml file: " + e.getMessage();
      catalogueParseResult =  FindAndParseCatalogueResult.createCatalogueEntryParseErrorResult(error);
    }

    return GetAndParseCatalogueResult.createFoundAndParsedResult(fileContentItem, catalogueParseResult);
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
