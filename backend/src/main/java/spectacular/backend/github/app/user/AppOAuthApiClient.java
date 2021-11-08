package spectacular.backend.github.app.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import spectacular.backend.github.domain.Account;
import spectacular.backend.github.domain.GetInstallationsResult;
import spectacular.backend.github.domain.UserAccessTokenRequest;
import spectacular.backend.github.domain.UserAccessTokenResult;

@Component
public class AppOAuthApiClient {

  private static final String OAUTH_LOGIN_ACCESS_TOKEN_PATH = "/login/oauth/access_token";

  private final RestTemplate restTemplate;

  public AppOAuthApiClient(@Value("${github.web.root-url}") String rootUrl,
                           RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder
        .rootUri(rootUrl)
        .requestFactory(HttpComponentsClientHttpRequestFactory.class)
        .build();
  }

  public UserAccessTokenResult requestUserAccessToken(UserAccessTokenRequest userAccessTokenRequest) {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(OAUTH_LOGIN_ACCESS_TOKEN_PATH);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    var entity = new HttpEntity<>(userAccessTokenRequest, headers);

    var response = restTemplate.postForEntity(uriComponentsBuilder.buildAndExpand().toUriString(), entity, UserAccessTokenResult.class);

    return response.getBody();
  }
}
