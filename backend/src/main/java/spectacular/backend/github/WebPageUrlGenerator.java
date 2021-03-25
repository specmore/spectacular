package spectacular.backend.github;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import spectacular.backend.common.RepositoryId;

@Service
public class WebPageUrlGenerator {
  private static final String REPO_CONTENT_PAGE = "/{repo}/blob/{ref}/{path}";

  private final String rootUrl;

  public WebPageUrlGenerator(@Value("${github.web.root-url}") String rootUrl) {
    this.rootUrl = rootUrl;
  }

  /**
   * Generates the URL to the GitHub web page for a given content file in a repository at a specific git history reference.
   *
   * @param repository the git repository of the content file
   * @param ref the git history ref at which to show the contents
   * @param contentFilePath the file path of the content in the git repository
   * @return a URI to a GitHub file contents web page
   * @throws URISyntaxException if the URL could not be generated
   */
  public URI generateContentPageUrl(RepositoryId repository, String ref, String contentFilePath) throws URISyntaxException {
    var gitHubBaseUri = new URI(rootUrl);
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(gitHubBaseUri).path(REPO_CONTENT_PAGE);
    return uriBuilder.buildAndExpand(repository.getNameWithOwner(), ref, contentFilePath).toUri();
  }
}
