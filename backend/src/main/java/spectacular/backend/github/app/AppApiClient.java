package spectacular.backend.github.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import spectacular.backend.github.domain.AccessTokenResult;
import spectacular.backend.github.domain.Installation;

@Component
public class AppApiClient {

  private static final String APP_INSTALLATION_ACCESS_TOKEN_PATH =
      "/app/installations/{installationId}/access_tokens";
  private static final String APP_INSTALLATION_PATH = "/app/installations/{installationId}";

  private final RestTemplate restTemplate;

  /**
   * A GitHub API v3 client that focuses on the "GitHub App" operations.
   *
   * @param rootUrl a config value for the root URI of the GitHub API v3
   * @param restTemplateBuilder a RestTemplateBuilder bean
   * @param gitHubAppAuthenticationHeaderRequestInterceptor a RestTemplate Request Interceptor to automatically inject JWT into the
   *     Authorisation request header needed for the access to the App API endpoints
   * @param appApiResponseErrorHandler a RestTemplate custom Response Error Handler for de-serialising the GitHub
   *     error message response body
   */
  public AppApiClient(@Value("${github.api.root-url}") String rootUrl,
                      RestTemplateBuilder restTemplateBuilder,
                      GitHubAppAuthenticationHeaderRequestInterceptor gitHubAppAuthenticationHeaderRequestInterceptor,
                      AppApiResponseErrorHandler appApiResponseErrorHandler) {
    this.restTemplate = restTemplateBuilder
        .rootUri(rootUrl)
        .requestFactory(HttpComponentsClientHttpRequestFactory.class)
        .additionalInterceptors(gitHubAppAuthenticationHeaderRequestInterceptor)
        .errorHandler(appApiResponseErrorHandler)
        .build();
  }

  /**
   * Requests a new API Access Token for an installation of this GitHub app.
   *
   * @param installationId the installation id of the access token is needed for
   * @return a result object containing the access token and when it will expire
   */
  public AccessTokenResult requestNewAppInstallationAccessToken(String installationId) {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(APP_INSTALLATION_ACCESS_TOKEN_PATH);
    String accessTokenUri = uriComponentsBuilder.buildAndExpand(installationId).toUriString();

    HttpHeaders headers = new HttpHeaders();
    HttpEntity entity = new HttpEntity(headers);

    var response = restTemplate.postForEntity(accessTokenUri, entity, AccessTokenResult.class);

    return response.getBody();
  }

  /**
   * Get details about the installation of this app against in a GitHub organisation's or user's account.
   *
   * @param installationId the id of the installation the details is needed for
   * @return returns an Installation object representing the details of the installation
   */
  public Installation getAppInstallation(String installationId) {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(APP_INSTALLATION_PATH);
    String appInstallationUri = uriComponentsBuilder.buildAndExpand(installationId).toUriString();

    HttpHeaders headers = new HttpHeaders();
    HttpEntity entity = new HttpEntity(headers);

    var response = restTemplate.exchange(appInstallationUri, HttpMethod.GET, entity, Installation.class);

    return response.getBody();
  }
}

