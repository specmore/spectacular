package spectacular.backend.github.app;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Date;

@Service
public class AppAuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AppAuthenticationService.class);
    private final String appId;
    private final String privateKeyFilePath;
    private final Duration jwtDuration;

    public AppAuthenticationService(@Value("${github.api.app.id}") String appId,
                                    @Value("${github.api.app.jwt-signing-key-file-path}") String privateKeyFilePath,
                                    @Value("#{T(java.time.Duration).parse('${github.api.app.jwt-duration}')}") Duration jwtDuration) {
        this.appId = appId;
        this.privateKeyFilePath = privateKeyFilePath;
        this.jwtDuration = jwtDuration;
    }

    public String generateJWT() throws JOSEException, IOException {
        Path path = Path.of(privateKeyFilePath);
        String privateKeyFileContent = Files.readString(path);

        var jwk = JWK.parseFromPEMEncodedObjects(privateKeyFileContent);

        if(!jwk.isPrivate() || jwk.getKeyType() != KeyType.RSA) return "";

        if(!(jwk instanceof RSAKey)) return "";

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

        var signedJWT = new SignedJWT(header, claims);
        signedJWT.sign(jwsSigner);

        logger.info("Generated and signed new App JWT for AppId: '{}' issued at '{}' and expiring at '{}'.", appId, claims.getIssueTime(), claims.getExpirationTime());

        return signedJWT.serialize();
    }
}
