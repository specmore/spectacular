package spectacular.backend.github.app

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import spectacular.backend.github.domain.AccessTokenResult
import spock.lang.Specification

import java.time.ZonedDateTime

class AppInstallationAuthenticationHeaderRequestInterceptorTest extends Specification {
    def body = new byte[0]
    def clientHttpRequestExecution = Mock(ClientHttpRequestExecution)
    def httpHeaders = Mock(HttpHeaders)
    def httpRequest = Mock(HttpRequest)
    def appInstallationService = Mock(AppInstallationService)
    def appInstallationContextProvider = Mock(AppInstallationContextProvider)
    def interceptor = new AppInstallationAuthenticationHeaderRequestInterceptor(appInstallationService, appInstallationContextProvider)

    def "Intercept adds installation access token to request bearer authorization header"() {
        given: "an access token"
        def accessToken = new AccessTokenResult("valid-test-token", ZonedDateTime.now().plusMinutes(10))
        and: "the app installation context is set"
        appInstallationContextProvider.getInstallationId() >> "1234"
        and: "a new http request with http headers"
        httpRequest.getHeaders() >> httpHeaders

        when: "the interception is executed"
        interceptor.intercept(httpRequest, body, clientHttpRequestExecution)

        then: "an access token is retrieved from the app installation service"
        1 * appInstallationService.getAccessTokenForInstallation("1234") >> accessToken
        and: "the access token is added as a bearer authorization request header"
        1 * httpHeaders.setBearerAuth(accessToken.getToken())
    }
}
