package spectacular.backend.app;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spectacular.backend.api.model.UserDetails;
import spectacular.backend.github.domain.GetInstallationsResult;
import spectacular.backend.github.domain.Installation;

@Service
public class UserSessionTokenService {
  private static final Logger logger = LoggerFactory.getLogger(UserSessionTokenService.class);
  private final String jwtSigningSecret;
  private final Duration jwtDuration;

  private static final String CLAIM_ORIGIN = "origin";
  private static final String CLAIM_FULL_NAME = "full_name";
  private static final String CLAIM_PROFILE_IMAGE_URL = "profile_image_url";
  private static final String CLAIM_INSTALLATIONS = "installations";

  public UserSessionTokenService(
      @Value("${security.authentication.jwt.signature-secret}") String jwtSigningSecret,
      @Value("#{T(java.time.Duration).parse('${security.authentication.jwt.duration}')}")
          Duration jwtDuration) {
    this.jwtSigningSecret = jwtSigningSecret;
    this.jwtDuration = jwtDuration;
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

  public String generateUserSessionToken(UserDetails userDetails, GetInstallationsResult getInstallationsResult) throws JOSEException {
    final var installationIds = getInstallationsResult.getInstallations().stream().map(Installation::getId).collect(
        Collectors.toList());

    final var jwsSigner = new MACSigner(this.jwtSigningSecret);
    final var header = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();

    Date expiryTime = Date.from(Instant.now().plus(this.jwtDuration));

    var claims = new JWTClaimsSet.Builder()
        .subject(userDetails.getUsername())
        .expirationTime(expiryTime)
        .claim(CLAIM_ORIGIN, "github")
        .claim(CLAIM_FULL_NAME, userDetails.getFullName())
        .claim(CLAIM_PROFILE_IMAGE_URL, userDetails.getProfileImageUrl())
        .claim(CLAIM_INSTALLATIONS, installationIds)
        .build();

    final var signedJwt = new SignedJWT(header, claims);
    signedJwt.sign(jwsSigner);

    logger.info("Generated and signed new User Session JWT for User: '{}' and expiring at '{}'.", userDetails.getUsername(), claims.getExpirationTime());

    return signedJwt.serialize();
  }

  public List<Long> getInstallationIds(String tokenValue) {
    try {
      var jwt = JWTParser.parse(tokenValue);
      var claims = jwt.getJWTClaimsSet();

      return (List<Long>) claims.getClaim(CLAIM_INSTALLATIONS);

    } catch (ParseException e) {
      logger.error("An error occurred while parsing a User Session token.", e);
    }

    return Collections.emptyList();
  }
}
