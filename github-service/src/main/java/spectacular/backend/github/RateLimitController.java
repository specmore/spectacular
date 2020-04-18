package spectacular.backend.github;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RateLimitController {
    private final RestApiClient restApiClient;

    public RateLimitController(RestApiClient restApiClient) {
        this.restApiClient = restApiClient;
    }

    @GetMapping(value = "api/github/rate_limit", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRateLimit() {
        return restApiClient.getRateLimit();
    }
}
