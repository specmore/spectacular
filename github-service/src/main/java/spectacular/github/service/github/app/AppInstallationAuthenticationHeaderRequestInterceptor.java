package spectacular.github.service.github.app;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AppInstallationAuthenticationHeaderRequestInterceptor implements ClientHttpRequestInterceptor {
    private final AppInstallationService appInstallationService;
    private final AppInstallationContextProvider appInstallationContextProvider;

    public AppInstallationAuthenticationHeaderRequestInterceptor(AppInstallationService appInstallationService, AppInstallationContextProvider appInstallationContextProvider) {
        this.appInstallationService = appInstallationService;
        this.appInstallationContextProvider = appInstallationContextProvider;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        var installationId = appInstallationContextProvider.getInstallationId();
        var accessToken = appInstallationService.getAccessTokenForInstallation(installationId);

        request.getHeaders().setBearerAuth(accessToken.getToken());

        return execution.execute(request, body);
    }
}
