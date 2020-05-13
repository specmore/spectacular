package spectacular.backend.github;

import java.time.Instant;
import java.util.List;
import java.util.StringJoiner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import spectacular.backend.common.Repository;
import spectacular.backend.github.app.AppInstallationAuthenticationHeaderRequestInterceptor;
import spectacular.backend.github.domain.ContentItem;
import spectacular.backend.github.domain.SearchCodeResults;
import spectacular.backend.github.graphql.GraphQlRequest;
import spectacular.backend.github.graphql.GraphQlResponse;

@Component
public class RestApiClient {
  private static final String GRAPH_QL = "/graphql";
  private static final String RATE_LIMIT = "/rate_limit";
  private static final String SEARCH_CODE_PATH = "/search/code";
  private static final String REPO_PATH = "/repos/{repo}";
  private static final String REPO_CONTENT_PATH = "/repos/{repo}/contents/{path}";
  private static final String REPO_COLLABORATORS_PATH = "/repos/{repo}/collaborators/{username}";

  private final RestTemplate restTemplate;

  /**
   * Constructs a GitHub REST API client.
   *
   * @param rootUrl the configuration value for the root-url (or base url) of the GitHub API.
   * @param restTemplateBuilder a RestTemplateBuilder bean used to create a new RestTemplate instance for the use in the REST API client
   * @param requestFactory the caching HttpComponentsClientHttpRequestFactory bean to be used by the RestTemplate to create HTTP requests
   * @param appInstallationAuthenticationHeaderRequestInterceptor an AppInstallationAuthenticationHeaderRequestInterceptor bean used to
   *     ensure all API requests have the necessary Authentication Header for the current App Installation context
   */
  public RestApiClient(@Value("${github.api.root-url}") String rootUrl,
                       RestTemplateBuilder restTemplateBuilder,
                       HttpComponentsClientHttpRequestFactory requestFactory,
                       AppInstallationAuthenticationHeaderRequestInterceptor appInstallationAuthenticationHeaderRequestInterceptor) {
    this.restTemplate = restTemplateBuilder
        .rootUri(rootUrl)
        .requestFactory(() -> requestFactory)
        .additionalInterceptors(appInstallationAuthenticationHeaderRequestInterceptor)
        .build();
  }

  /**
   * Gets the content of a file in a repository at a specific ref from the API.
   *
   * @param repo the repository the file belongs to
   * @param path the path of the file
   * @param ref the reference to the commit at which to take the contents from
   * @return a ContentItem representing the details and encoded contents of the file
   */
  public ContentItem getRepositoryContent(Repository repo, String path, String ref) {
    UriComponentsBuilder uriComponentsBuilder =
        UriComponentsBuilder.fromUriString(REPO_CONTENT_PATH);
    if (ref != null && ref.length() > 0) {
      uriComponentsBuilder.queryParam("ref", ref);
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    HttpEntity entity = new HttpEntity(headers);

    String contentUri =
        uriComponentsBuilder.buildAndExpand(repo.getNameWithOwner(), path).toUriString();
    var response = restTemplate.exchange(contentUri, HttpMethod.GET, entity, ContentItem.class);

    var contentItem = response.getBody();
    var lastModified = Instant.ofEpochMilli(response.getHeaders().getLastModified());
    contentItem.setLastModified(lastModified);

    return contentItem;
  }

  /**
   * Finds files with a given filename, extension, path and organisation/repository.
   *
   * @param filename the filename to find
   * @param fileExtensions a list of possible extensions the files can have
   * @param path the path of the files to find
   * @param org limit to the repositories owned by the specified organisation
   * @param repo limit the search to the specific repository
   * @return a SearchCodeResults object representing the result of the search
   */
  public SearchCodeResults findFiles(String filename, List<String> fileExtensions, String path, String org,
                                     Repository repo) {
    StringJoiner joiner = new StringJoiner("+");
    if (filename != null && filename.length() > 0) {
      joiner.add("filename:" + filename);
    }
    if (fileExtensions != null && fileExtensions.size() > 0) {
      for (var fileExtension : fileExtensions) {
        joiner.add("extension:" + fileExtension);
      }
    }
    if (path != null && path.length() > 0) {
      joiner.add("path:" + path);
    }
    if (repo != null) {
      joiner.add("repo:" + repo.getNameWithOwner());
    } else if (org != null && org.length() > 0) {
      joiner.add("org:" + org);
    }

    UriComponentsBuilder uriComponentsBuilder =
        UriComponentsBuilder.fromUriString(SEARCH_CODE_PATH).queryParam("q", joiner.toString());

    HttpHeaders headers = new HttpHeaders();
    HttpEntity entity = new HttpEntity(headers);

    ResponseEntity<SearchCodeResults> response = restTemplate
        .exchange(uriComponentsBuilder.build().toUriString(), HttpMethod.GET, entity, SearchCodeResults.class);
    return response.getBody();
  }

  /**
   * Check if a user is a collaborator for a given repository.
   *
   * @param repo the repository to check against
   * @param username the name of the user to check is a collaborator
   * @return true if the user is a collaborator of the repository
   */
  public boolean isUserRepositoryCollaborator(Repository repo, String username) {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(REPO_COLLABORATORS_PATH);

    HttpHeaders headers = new HttpHeaders();
    HttpEntity entity = new HttpEntity(headers);

    String contentUri = uriComponentsBuilder.buildAndExpand(repo.getNameWithOwner(), username).toUriString();
    ResponseEntity<Void> response = restTemplate.exchange(contentUri, HttpMethod.GET, entity, Void.class);
    return response.getStatusCode().is2xxSuccessful();
  }

  /**
   * Get details about a repository.
   *
   * @param repo the repository identifier to get more information about
   * @return a Repository object with the information retrieved from the API
   */
  public spectacular.backend.github.domain.Repository getRepository(Repository repo) {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(REPO_PATH);

    HttpHeaders headers = new HttpHeaders();
    HttpEntity entity = new HttpEntity(headers);

    String contentUri = uriComponentsBuilder.buildAndExpand(repo.getNameWithOwner()).toUriString();
    ResponseEntity<spectacular.backend.github.domain.Repository> response = restTemplate
        .exchange(contentUri, HttpMethod.GET, entity, spectacular.backend.github.domain.Repository.class);
    return response.getBody();
  }

  /**
   * Executes a GraphQL query against the GitHub GraphQL API.
   *
   * @param graphQlRequest the GraphQlRequest containing the query to be run
   * @return the GraphQlResponse object with the query results
   */
  public GraphQlResponse graphQlQuery(GraphQlRequest graphQlRequest) {
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<GraphQlRequest> entity = new HttpEntity<>(graphQlRequest, headers);

    ResponseEntity<GraphQlResponse> response = restTemplate.postForEntity(GRAPH_QL, entity, GraphQlResponse.class);
    return response.getBody();
  }

  /**
   * Gets the current GitHUb API rate limit counter values for the context of the current GitHub App Installation.
   *
   * @return the full JSON response payload as a String
   */
  public String getRateLimit() {
    final String response = restTemplate.getForObject(RATE_LIMIT, String.class);
    return response;
  }
}
