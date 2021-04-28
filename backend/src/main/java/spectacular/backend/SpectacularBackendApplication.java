package spectacular.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.app.AppInstallationContextProvider;
import spectacular.backend.github.pullrequests.PullRequestRepository;

@SpringBootApplication
public class SpectacularBackendApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpectacularBackendApplication.class, args);
  }

  @Bean
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public AppInstallationContextProvider appInstallationContextProviderRequestScopedBean() {
    return new AppInstallationContextProvider();
  }

  @Bean
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public PullRequestRepository pullRequestRepositoryRequestScopedBean(RestApiClient restApiClient) {
    return new PullRequestRepository(restApiClient);
  }
}
