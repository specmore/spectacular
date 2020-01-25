package spectacular.github.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import spectacular.github.service.security.JWTCookieToAuthorizationHeaderFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${security.authentication.jwt.signature-secret}")
    private String jwtSigningSecret;

    @Value("${security.authentication.jwt.cookie-name}")
    private String jwtCookieName;

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] secretBytes = jwtSigningSecret.getBytes(StandardCharsets.UTF_8);;
        SecretKey secretKey = new SecretKeySpec(secretBytes, "HmacSHA512");
        return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS512).build();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var jwtCookieToAuthorizationHeaderFilter = new JWTCookieToAuthorizationHeaderFilter(this.jwtCookieName);

        http
            .csrf().disable()
            .logout().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()
                .anyRequest().authenticated()
                .and()
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
        .addFilterBefore(jwtCookieToAuthorizationHeaderFilter, BearerTokenAuthenticationFilter.class);
    }
}
