package spectacular.github.service.github.app

import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.test.web.client.MockRestServiceServer
import spock.lang.Specification

import java.time.ZonedDateTime

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(components = AppApiClient.class, properties = "github.api.app.installation.id=99")
class AppApiClientTest extends Specification {
    @Autowired
    private AppApiClient client

    @Autowired
    private MockRestServiceServer server

    @Value('${github.api.app.installation.id}')
    private String appInstallationId

    @SpringBean
    GitHubAppAuthenticationHeaderRequestInterceptor gitHubAppAuthenticationHeaderRequestInterceptor = Mock()

    def "CreateNewAppInstallationAccessToken"() {
        given: "a valid access token response"
        def responseContent = "{\n" +
                "    \"token\": \"v1.d06c824bec3807ca411fa6cfc6fd53a37ac54be3\",\n" +
                "    \"expires_at\": \"2019-12-03T00:11:08Z\"\n" +
                "}"
        this.server.expect(requestTo("/installations/${appInstallationId}/access_tokens"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(responseContent, MediaType.APPLICATION_JSON));

        when: "the access token is retrieved by the AppApiClient"
        def accessToken = client.createNewAppInstallationAccessToken()

        then: "the access token object has the token and expiration time"
        accessToken
        accessToken.getToken() == "v1.d06c824bec3807ca411fa6cfc6fd53a37ac54be3"
        accessToken.getExpirationDateTime().toLocalDateTime() == ZonedDateTime.parse("2019-12-03T00:11:08Z").toLocalDateTime()

        and: "the AppAuthentication interceptor is used during the request"
        1 * gitHubAppAuthenticationHeaderRequestInterceptor.intercept(_,_,_) >> { HttpRequest request, byte[] body, ClientHttpRequestExecution execution ->
            execution.execute(request, body)
        }
    }
}
