package spectacular.backend.specs;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import spectacular.backend.api.model.OpenApiSpecParseResult;
import spectacular.backend.common.Repository;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.specs.openapi.OpenApiParser;

@Service
public class SpecService {
  private static final Logger logger = LoggerFactory.getLogger(SpecService.class);
  private final RestApiClient restApiClient;

  public SpecService(RestApiClient restApiClient) {
    this.restApiClient = restApiClient;
  }

  /**
   * Gets the details of a specific spec file and the parsed OpenAPI YAML contents of the file.
   *
   * @param repoId the repository the spec file belongs to
   * @param filePath the file path of the spec file
   * @param ref a reference to the commit in the git repository's history at which to take the file's contents
   * @return a SpecItem object containing the file details and parsed OpenAPI YAML of the file's contents
   */
  public spectacular.backend.api.model.SpecItem getSpecItem(Repository repoId, String filePath, String ref) {
    OpenApiSpecParseResult parseResult = null;
    URI htmlUrl = null;
    String sha = null;
    OffsetDateTime lastModified = null;
    try {
      var contentItem = restApiClient.getRepositoryContent(repoId, filePath, ref);
      htmlUrl = contentItem.getHtml_url();
      sha = contentItem.getSha();
      lastModified = contentItem.getLastModified();
      parseResult = OpenApiParser.parseYaml(contentItem.getDecodedContent());
    } catch (HttpClientErrorException.NotFound nf) {
      logger.debug("Failed to retrieve file contents due an file not found on the github api.", nf);
      parseResult = new OpenApiSpecParseResult()
          .errors(Collections.singletonList("The spec file could not be found."));
    } catch (UnsupportedEncodingException e) {
      logger.debug("Failed to decode file contents due encoding type not support.", e);
      parseResult = new OpenApiSpecParseResult()
          .errors(Collections.singletonList("The spec file contents from GitHub could not be decoded."));
    }

    var fullPath = String.join("/", repoId.getNameWithOwner(), filePath);
    var id = String.join("/", repoId.getNameWithOwner(), ref, filePath);
    return new spectacular.backend.api.model.SpecItem()
        .id(id)
        .fullPath(fullPath)
        .ref(ref)
        .htmlUrl(htmlUrl)
        .sha(sha)
        .lastModified(lastModified)
        .parseResult(parseResult);
  }
}
