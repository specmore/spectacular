package spectacular.backend.github.app.user;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spectacular.backend.api.model.UserDetails;
import spectacular.backend.github.domain.UserAccessTokenRequest;

@Service
public class AppUserAuthenticationService {
  private static final Logger logger = LoggerFactory.getLogger(AppUserAuthenticationService.class);

  private final String clientId;
  private final String clientSecret;
  private final String jwtSigningSecret;
  private final Duration jwtDuration;
  private final AppUserApiClient appUserApiClient;
  private final AppOAuthApiClient appOAuthApiClient;

  /**
   * A service for authenticating GitHub Users for a GitHub App.
   * @param clientId a config value of the Client Id for the GitHub app representing this application instance
   * @param jwtSigningSecret
   * @param jwtDuration
   * @param appUserApiClient
   */
  public AppUserAuthenticationService(@Value("${github.api.app.client-id}") String clientId,
                                      @Value("${github.api.app.client-secret}") String clientSecret,
                                      @Value("${security.authentication.jwt.signature-secret}") String jwtSigningSecret,
                                      @Value("#{T(java.time.Duration).parse('${security.authentication.jwt.duration}')}")
                                          Duration jwtDuration,
                                      AppUserApiClient appUserApiClient,
                                      AppOAuthApiClient appOAuthApiClient) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.jwtSigningSecret = jwtSigningSecret;
    this.jwtDuration = jwtDuration;
    this.appUserApiClient = appUserApiClient;
    this.appOAuthApiClient = appOAuthApiClient;
  }

  public CreateUserSessionResult createUserSession(String code) {
    final var userAccessTokenRequest = new UserAccessTokenRequest(this.clientId, this.clientSecret, code);
    final var userAccessTokenResult = this.appOAuthApiClient.requestUserAccessToken(userAccessTokenRequest);

    var user = this.appUserApiClient.getUser(userAccessTokenResult.getAccessToken());
    var installations = this.appUserApiClient.getInstallationsAccessibleByUser(userAccessTokenResult.getAccessToken());
    var userDetails = new UserDetails().username(user.getLogin());

    String userSessionToken = null;
    try {
      userSessionToken = generateUserSessionToken(userDetails.getUsername());
    } catch (JOSEException e) {
      logger.error("An error occurred while generating a new User Session JWT for User: '{}'.", userDetails.getUsername(), e);
    }

    return new CreateUserSessionResult(userSessionToken, userDetails);
  }

  public String getClientId() {
    return clientId;
  }

  private String generateUserSessionToken(String username) throws JOSEException {
    final var jwsSigner = new MACSigner(this.jwtSigningSecret);
    final var header = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();

    Date expiryTime = Date.from(Instant.now().plus(this.jwtDuration));

    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .subject(username)
        .expirationTime(expiryTime)
        .claim("origin", "github")
        .build();

    final var signedJwt = new SignedJWT(header, claims);
    signedJwt.sign(jwsSigner);

    logger.info("Generated and signed new User Session JWT for User: '{}' and expiring at '{}'.", username, claims.getExpirationTime());

    return signedJwt.serialize();
  }
}
