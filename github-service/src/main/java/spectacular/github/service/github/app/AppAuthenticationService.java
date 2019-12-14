package spectacular.github.service.github.app;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Service
public class AppAuthenticationService {
    private final String appId;
    private final String privateKeyFilePath;

    public AppAuthenticationService(@Value("${github.api.app.id}") String appId, @Value("${github.api.app.private-key-file-path}") String privateKeyFilePath) {
        this.appId = appId;
        this.privateKeyFilePath = privateKeyFilePath;
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
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(appId)
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + 1000*60*10)) // expires in 10 minutes
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).build();

        var signedJWT = new SignedJWT(header, claims);
        signedJWT.sign(jwsSigner);

        return signedJWT.serialize();
    }
}
