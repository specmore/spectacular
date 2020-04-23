package spectacular.backend.github.app;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppAuthenticationService {
  private static final Logger logger = LoggerFactory.getLogger(AppAuthenticationService.class);
  private final String appId;
  private final String privateKeyFilePath;
  private final Duration jwtDuration;

  /**
   * Functionality needed to Authenticate as the configured GitHub App.
   *
   * @param appId a config value of the id for the GitHub app representing this application instance
   * @param privateKeyFilePath config value of the file path to the RSA private key provided by GitHub for this app.
   * @param jwtDuration a config value for how long each JWT created should be valid for
   */
  public AppAuthenticationService(@Value("${github.api.app.id}") String appId,
                                  @Value("${github.api.app.jwt-signing-key-file-path}") String privateKeyFilePath,
                                  @Value("#{T(java.time.Duration).parse('${github.api.app.jwt-duration}')}") Duration jwtDuration) {
    this.appId = appId;
    this.privateKeyFilePath = privateKeyFilePath;
    this.jwtDuration = jwtDuration;
  }

  /**
   * Generate a RSA signed, RS256 encoded JWT for identifying this app when making requests to the GitHub App API.
   *
   * @return a string representation of the JWT payload
   * @throws JOSEException if a problem occurs during the parsing of the RSA private key or during JWT signing
   * @throws IOException if a problem occurs when reading teh RSA private key file at the configured path
   */
  public String generateJwt() throws JOSEException, IOException {
    Path path = Path.of(privateKeyFilePath);
    String privateKeyFileContent = Files.readString(path);

    var jwk = JWK.parseFromPEMEncodedObjects(privateKeyFileContent);

    if (!jwk.isPrivate() || jwk.getKeyType() != KeyType.RSA) {
      return "";
    }

    if (!(jwk instanceof RSAKey)) {
      return "";
    }

    var rsaKey = (RSAKey) jwk;

    var jwsSigner = new RSASSASigner(rsaKey);

    var now = new Date();
    var expiration = new Date(now.getTime() + jwtDuration.toMillis());
    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .issuer(appId)
        .issueTime(now)
        .expirationTime(expiration)
        .build();

    JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).build();

    var signedJwt = new SignedJWT(header, claims);
    signedJwt.sign(jwsSigner);

    logger.info("Generated and signed new App JWT for AppId: '{}' issued at '{}' and expiring at '{}'.", appId, claims.getIssueTime(),
        claims.getExpirationTime());

    return signedJwt.serialize();
  }
}
