package spectacular.backend.github.app.user;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
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

  private static final String CLAIM_ORIGIN = "origin";
  private static final String CLAIM_FULL_NAME = "full-name";
  private static final String CLAIM_PROFILE_IMAGE_URL = "profile-image-url";

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
    var userDetails = new UserDetails()
        .username(user.getLogin())
        .fullName(user.getName())
        .profileImageUrl(user.getAvatarUrl());

    String userSessionToken = null;
    try {
      userSessionToken = generateUserSessionToken(userDetails);
    } catch (JOSEException e) {
      logger.error("An error occurred while generating a new User Session JWT for User: '{}'.", userDetails.getUsername(), e);
    }

    return new CreateUserSessionResult(userSessionToken, userDetails);
  }

  public String getClientId() {
    return clientId;
  }

  public UserDetails populateUserDetailsFromSessionToken(String token) {
    try {
      var jwt = JWTParser.parse(token);
      var claims = jwt.getJWTClaimsSet();

      return new UserDetails()
          .username(claims.getSubject())
          .fullName(claims.getStringClaim(CLAIM_FULL_NAME))
          .profileImageUrl(claims.getStringClaim(CLAIM_PROFILE_IMAGE_URL));
    } catch (ParseException e) {
      logger.error("An error occurred while parsing a User Session token.", e);
    }

    return null;
  }

  private String generateUserSessionToken(UserDetails userDetails) throws JOSEException {
    final var jwsSigner = new MACSigner(this.jwtSigningSecret);
    final var header = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();

    Date expiryTime = Date.from(Instant.now().plus(this.jwtDuration));

    var claims = new JWTClaimsSet.Builder()
        .subject(userDetails.getUsername())
        .expirationTime(expiryTime)
        .claim(CLAIM_ORIGIN, "github")
        .claim(CLAIM_FULL_NAME, userDetails.getFullName())
        .claim(CLAIM_PROFILE_IMAGE_URL, userDetails.getProfileImageUrl())
        .build();

    final var signedJwt = new SignedJWT(header, claims);
    signedJwt.sign(jwsSigner);

    logger.info("Generated and signed new User Session JWT for User: '{}' and expiring at '{}'.", userDetails.getUsername(), claims.getExpirationTime());

    return signedJwt.serialize();
  }
}
