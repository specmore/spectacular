package spectacular.github.service.github.app

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpHeaders
import org.springframework.http.client.ClientHttpRequestExecution
import spock.lang.Specification

class GitHubAppAuthenticationHeaderRequestInterceptorTest extends Specification {
    def appAuthService = Mock(AppAuthenticationService)
    def body = new byte[0]
    def clientHttpRequestExecution = Mock(ClientHttpRequestExecution)
    def httpHeaders = Mock(HttpHeaders)
    def httpRequest = Mock(HttpRequest)
    def interceptor = new GitHubAppAuthenticationHeaderRequestInterceptor(appAuthService)

    def "Interceptor adds JWT to request bearer authorization header"() {
        given: "a JWT token"
        def jwtToken = "test-token"
        and: "a new http request with http headers"
        httpRequest.getHeaders() >> httpHeaders

        when: "the interceptor is executed"
        interceptor.intercept(httpRequest, body, clientHttpRequestExecution)

        then: "the app authentication service generates a JWT"
        1 * appAuthService.generateJWT() >> jwtToken
        and: "the JWT is added to bearer authorization request header"
        1 * httpHeaders.setBearerAuth(jwtToken)
    }

    def "Interceptor adds github preview accept header to request"() {
        given: "a github api accept header"
        def acceptHeader = "application/vnd.github.machine-man-preview+json"
        and: "a new http request with http headers"
        httpRequest.getHeaders() >> httpHeaders

        when: "the interceptor is executed"
        interceptor.intercept(httpRequest, body, clientHttpRequestExecution)

        then: "the accept header is added to the request"
        1 * httpHeaders.set("Accept", acceptHeader)
    }

    def "Interceptor continues request execution"() {
        given: "a new http request with http headers"
        httpRequest.getHeaders() >> httpHeaders

        when: "the interceptor is executed"
        interceptor.intercept(httpRequest, body, clientHttpRequestExecution)

        then: "the next request execution is invoked"
        1 * clientHttpRequestExecution.execute(httpRequest, body)
    }
}
