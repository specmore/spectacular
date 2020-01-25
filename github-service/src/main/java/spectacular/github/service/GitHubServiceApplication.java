package spectacular.github.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.context.WebApplicationContext;
import spectacular.github.service.github.app.AppInstallationContextProvider;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class GitHubServiceApplication {
	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public AppInstallationContextProvider appInstallationContextProviderRequestScopedBean() {
		return new AppInstallationContextProvider();
	}

	@Value("${security.authentication.jwt.signature-secret}")
	private String jwtSigningSecret;

	@Value("${security.authentication.jwt.cookies-name}")
	private String jwtCookieName;

	@Bean
	public JwtDecoder jwtDecoder() {
		byte[] secretBytes = jwtSigningSecret.getBytes(StandardCharsets.UTF_8);;
		//byte[] decodedKey = Base64.getDecoder().decode(this.jwtSigningSecret);
		SecretKey secretKey = new SecretKeySpec(secretBytes, "HmacSHA512");
		return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS512).build();
	}

	public static void main(String[] args) {
		SpringApplication.run(GitHubServiceApplication.class, args);
	}

}
