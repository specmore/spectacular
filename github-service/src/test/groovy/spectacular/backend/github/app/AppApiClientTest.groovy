package spectacular.backend.github.app

import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.client.MockRestServiceServer
import spock.lang.Specification

import java.time.ZonedDateTime

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

@RestClientTest(components = AppApiClient.class)
class AppApiClientTest extends Specification {
    @Autowired
    private AppApiClient client

    @Autowired
    private MockRestServiceServer server

    @SpringBean
    GitHubAppAuthenticationHeaderRequestInterceptor gitHubAppAuthenticationHeaderRequestInterceptor = Mock()

    @SpringBean
    AppApiResponseErrorHandler appApiResponseErrorHandler = new AppApiResponseErrorHandler(new MappingJackson2HttpMessageConverter())

    def "CreateNewAppInstallationAccessToken returns a valid access token result for valid response"() {
        given: "a valid access token response"
        def responseContent = "{\n" +
                "    \"token\": \"v1.d06c824bec3807ca411fa6cfc6fd53a37ac54be3\",\n" +
                "    \"expires_at\": \"2019-12-03T00:11:08Z\"\n" +
                "}"
        def String appInstallationId = 101
        this.server.expect(requestTo("/installations/${appInstallationId}/access_tokens"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(responseContent, MediaType.APPLICATION_JSON));

        when: "the access token is retrieved by the AppApiClient"
        def accessToken = client.createNewAppInstallationAccessToken(appInstallationId)

        then: "the access token object has the token and expiration time"
        accessToken
        accessToken.getToken() == "v1.d06c824bec3807ca411fa6cfc6fd53a37ac54be3"
        accessToken.getExpirationDateTime().toLocalDateTime() == ZonedDateTime.parse("2019-12-03T00:11:08Z").toLocalDateTime()

        and: "the AppAuthentication interceptor is used during the request"
        1 * gitHubAppAuthenticationHeaderRequestInterceptor.intercept(_,_,_) >> { HttpRequest request, byte[] body, ClientHttpRequestExecution execution ->
            execution.execute(request, body)
        }
    }

    //tried to replicate this issue, but it must be a problem with the connection client that is not used for RestClientTest tests
    def "AppApiResponseErrorHandler throws an AppApiUnauthorizedErrorException for an unauthorised response"() {
        given: "a valid access token response"
        def responseContent = "{\n" +
                "    \"message\": \"bla bla bla\"\n" +
                "}"
        def String appInstallationId = 101
        this.server.expect(requestTo("/installations/${appInstallationId}/access_tokens"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withUnauthorizedRequest().body(responseContent).contentType(MediaType.APPLICATION_JSON));

        when: "the access token is retrieved by the AppApiClient"
        def accessToken = client.createNewAppInstallationAccessToken(appInstallationId)

        then: "an AppApiUnauthorizedErrorException is thrown with the response message set"
        AppApiUnauthorizedErrorException e = thrown()
        e.getUnauthorizedError().getMessage() == "bla bla bla"

        and: "the AppAuthentication interceptor is used during the request"
        1 * gitHubAppAuthenticationHeaderRequestInterceptor.intercept(_,_,_) >> { HttpRequest request, byte[] body, ClientHttpRequestExecution execution ->
            execution.execute(request, body)
        }
    }
}
