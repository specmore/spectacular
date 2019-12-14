package spectacular.github.service.github

import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.test.web.client.MockRestServiceServer
import spectacular.github.service.github.app.AppInstallationAuthenticationHeaderRequestInterceptor
import spectacular.github.service.github.domain.Repository
import spock.lang.Specification

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(components = RestApiClient.class)
class RestApiClientTest extends Specification {
    @Autowired
    private RestApiClient client

    @Autowired
    private MockRestServiceServer server

    @SpringBean
    AppInstallationAuthenticationHeaderRequestInterceptor appInstallationAuthenticationHeaderRequestInterceptor = Mock()

    def "GetRepositoryContent"() {
        given: "a content file to fetch"
        def repo = new Repository("testOwner", "testRepo")
        def filePath = "test-file.yaml"
        and: "a valid raw content response"
        def responseContent = "test response content"
        and: "the app installation authentication header interceptor to be used for the request"
        1 * appInstallationAuthenticationHeaderRequestInterceptor.intercept(_,_,_) >> { HttpRequest request, byte[] body, ClientHttpRequestExecution execution ->
            execution.execute(request, body)
        }

        expect: "the github content api endpoint to be called with a get and the raw content accept header"
        def repoNameWithOwner = repo.getNameWithOwner()
        this.server.expect(requestTo("/repos/$repoNameWithOwner/contents/$filePath"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Accept", "application/vnd.github.3.raw"))
                .andRespond(withSuccess(responseContent, MediaType.APPLICATION_JSON));

        and: "the raw content to be returned by the client for a successful response"
        def contentResult = client.getRepositoryContent(repo, filePath, null)
        contentResult
        contentResult == responseContent
    }
}
