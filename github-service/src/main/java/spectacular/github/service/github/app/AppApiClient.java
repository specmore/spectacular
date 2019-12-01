package spectacular.github.service.github.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class AppApiClient {

    private static final String APP_INSTALLATION_PATH = "installations/{installationId}/access_tokens";

    private final RestTemplate restTemplate;
    private final String appInstallationId;

    public AppApiClient(@Value("${github.api.root-url}") String rootUrl, RestTemplateBuilder restTemplateBuilder, GitHubAppAuthenticationHeaderRequestInterceptor gitHubAppAuthenticationHeaderRequestInterceptor, @Value("${github.api.app.installation.id}") String appInstallationId) {
        this.appInstallationId = appInstallationId;
        this.restTemplate = restTemplateBuilder.rootUri(rootUrl).additionalInterceptors(gitHubAppAuthenticationHeaderRequestInterceptor).build();
    }

    public AccessTokenResult createNewAppInstallationAccessToken() {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(APP_INSTALLATION_PATH);
        String accessTokenUri = uriComponentsBuilder.buildAndExpand(appInstallationId).toUriString();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);

        var response = restTemplate.postForEntity(accessTokenUri, entity, AccessTokenResult.class);

        return response.getBody();
    }
}

