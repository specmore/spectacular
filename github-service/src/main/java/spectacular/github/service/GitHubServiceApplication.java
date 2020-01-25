package spectacular.github.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import spectacular.github.service.github.app.AppInstallationContextProvider;

@SpringBootApplication
public class GitHubServiceApplication {
	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public AppInstallationContextProvider appInstallationContextProviderRequestScopedBean() {
		return new AppInstallationContextProvider();
	}

	public static void main(String[] args) {
		SpringApplication.run(GitHubServiceApplication.class, args);
	}
}
