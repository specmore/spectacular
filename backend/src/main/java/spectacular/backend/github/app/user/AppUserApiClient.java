package spectacular.backend.github.app.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import spectacular.backend.github.domain.UserAccessTokenResult;

public class AppUserApiClient {

  private static final String OAUTH_LOGIN_ACCESS_TOKEN_PATH = "/login/oauth/access_token";

  private final RestTemplate restTemplate;

  public AppUserApiClient(@Value("${github.api.root-url}") String rootUrl,
                          RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder
        .rootUri(rootUrl)
        .requestFactory(HttpComponentsClientHttpRequestFactory.class)
        .build();
  }

  public UserAccessTokenResult requestUserAccessToken(String clientId, String clientSecret, String code) {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(OAUTH_LOGIN_ACCESS_TOKEN_PATH);
    uriComponentsBuilder.queryParam("client_id", clientId);
    uriComponentsBuilder.queryParam("client_secret", clientSecret);
    uriComponentsBuilder.queryParam("code", code);
    String accessTokenUri = uriComponentsBuilder.buildAndExpand().toUriString();

    HttpHeaders headers = new HttpHeaders();
    HttpEntity entity = new HttpEntity(headers);

    var response = restTemplate.postForEntity(accessTokenUri, entity, UserAccessTokenResult.class);

    return response.getBody();
  }
}
