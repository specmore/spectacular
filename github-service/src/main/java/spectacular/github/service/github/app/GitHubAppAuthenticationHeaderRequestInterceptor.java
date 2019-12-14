package spectacular.github.service.github.app;

import com.nimbusds.jose.JOSEException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GitHubAppAuthenticationHeaderRequestInterceptor implements ClientHttpRequestInterceptor {

    private final AppAuthenticationService appAuthenticationService;
    private static final String APP_INSTALLATION_ACCEPT_HEADER = "application/vnd.github.machine-man-preview+json";
    Logger logger = LoggerFactory.getLogger(GitHubAppAuthenticationHeaderRequestInterceptor.class);

    @Autowired
    public GitHubAppAuthenticationHeaderRequestInterceptor(AppAuthenticationService appAuthenticationService) {
        this.appAuthenticationService = appAuthenticationService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            var jwt = appAuthenticationService.generateJWT();
            request.getHeaders().setBearerAuth(jwt);
        } catch (JOSEException e) {
            logger.error("app JWT creation failed", e);
        }

        request.getHeaders().set("Accept", APP_INSTALLATION_ACCEPT_HEADER);

        ClientHttpResponse response = execution.execute(request, body);
        return response;
    }
}
