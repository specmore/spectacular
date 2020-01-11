package spectacular.github.service.github.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class AppApiClient {

    private static final String APP_INSTALLATION_PATH = "/installations/{installationId}/access_tokens";

    private final RestTemplate restTemplate;

    public AppApiClient(@Value("${github.api.root-url}") String rootUrl, RestTemplateBuilder restTemplateBuilder, GitHubAppAuthenticationHeaderRequestInterceptor gitHubAppAuthenticationHeaderRequestInterceptor, AppApiResponseErrorHandler appApiResponseErrorHandler) {
        this.restTemplate = restTemplateBuilder
                .rootUri(rootUrl)
                .requestFactory(HttpComponentsClientHttpRequestFactory.class)
                .additionalInterceptors(gitHubAppAuthenticationHeaderRequestInterceptor)
                .errorHandler(appApiResponseErrorHandler)
                .build();
    }

    public AccessTokenResult createNewAppInstallationAccessToken(String installationId) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(APP_INSTALLATION_PATH);
        String accessTokenUri = uriComponentsBuilder.buildAndExpand(installationId).toUriString();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);

        var response = restTemplate.postForEntity(accessTokenUri, entity, AccessTokenResult.class);

        return response.getBody();
    }
}

