package spectacular.github.service.github.app

import com.nimbusds.jwt.SignedJWT
import spock.lang.Specification

class AppAuthenticationServiceTest extends Specification {
    def "GenerateJWT"() {
        given: "A GitHub App private key pem file path"
        def pemFilePath = "src/test/resources/spectacular-test-app.2019-12-01.private-key.pem"
        and: "App id"
        def appId = "123456"
        and: "a AppAuthenticationService is create"
        def appAuthenticationService = new AppAuthenticationService(appId, pemFilePath)

        when: "a JWT is generated"
        def jwtContents = appAuthenticationService.generateJWT()

        then: "the JWT issuer is the app Id"
        def jwt = SignedJWT.parse(jwtContents)
        jwt.JWTClaimsSet.issuer == appId
        and: "the JWT expiration time is in the future"
        jwt.JWTClaimsSet.expirationTime > new Date()
    }
}
