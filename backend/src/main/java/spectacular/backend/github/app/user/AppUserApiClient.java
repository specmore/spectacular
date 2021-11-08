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

@Component
public class AppUserApiClient {

  private static final String USER_PATH = "/user";
  private static final String USER_ACCESSIBLE_INSTALLATIONS_PATH = "/user/installations";

  private final RestTemplate restTemplate;

  public AppUserApiClient(@Value("${github.api.root-url}") String rootUrl,
                          RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder
        .rootUri(rootUrl)
        .requestFactory(HttpComponentsClientHttpRequestFactory.class)
        .build();
  }

  public Account getUser(String userAccessToken) {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(USER_PATH);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(userAccessToken);
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    var entity = new HttpEntity<>(headers);

    var response = restTemplate.exchange(uriComponentsBuilder.buildAndExpand().toUriString(), HttpMethod.GET, entity, Account.class);

    return response.getBody();

  }

  public GetInstallationsResult getInstallationsAccessibleByUser(String userAccessToken) {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(USER_ACCESSIBLE_INSTALLATIONS_PATH);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(userAccessToken);
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    var entity = new HttpEntity<>(headers);

    var response = restTemplate.exchange(uriComponentsBuilder.buildAndExpand().toUriString(), HttpMethod.GET, entity, GetInstallationsResult.class);

    return response.getBody();
  }
}
