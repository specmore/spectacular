package spectacular.github.service.github

import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.test.web.client.MockRestServiceServer
import spectacular.github.service.github.app.AppInstallationAuthenticationHeaderRequestInterceptor
import spectacular.github.service.common.Repository
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
        def repo = new Repository("testOwner", "testRepo", null)
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

    def "findFiles"() {
        given: "a filename to find"
        def filename = "spectacular-app-config.yaml"
        and: "a valid raw content response"
        def responseContent = "{\n" +
                "    \"total_count\": 1,\n" +
                "    \"incomplete_results\": false,\n" +
                "    \"items\": [\n" +
                "        {\n" +
                "            \"name\": \"spectacular-app-config.yaml\",\n" +
                "            \"path\": \"spectacular-app-config.yaml\",\n" +
                "            \"sha\": \"55fc2e9819841ea2d01eb665681cd80b0792a3ba\",\n" +
                "            \"url\": \"https://api.github.com/repositories/223820527/contents/spectacular-app-config.yaml?ref=3f063baa7577892f75372857ccc1821f720b8d40\",\n" +
                "            \"git_url\": \"https://api.github.com/repositories/223820527/git/blobs/55fc2e9819841ea2d01eb665681cd80b0792a3ba\",\n" +
                "            \"html_url\": \"https://github.com/pburls/specs-app/blob/3f063baa7577892f75372857ccc1821f720b8d40/spectacular-app-config.yaml\",\n" +
                "            \"repository\": {\n" +
                "                \"id\": 223820527,\n" +
                "                \"node_id\": \"MDEwOlJlcG9zaXRvcnkyMjM4MjA1Mjc=\",\n" +
                "                \"name\": \"specs-app\",\n" +
                "                \"full_name\": \"pburls/specs-app\",\n" +
                "                \"private\": true,\n" +
                "                \"owner\": {\n" +
                "                    \"login\": \"pburls\",\n" +
                "                    \"id\": 11502284,\n" +
                "                    \"node_id\": \"MDQ6VXNlcjExNTAyMjg0\",\n" +
                "                    \"avatar_url\": \"https://avatars2.githubusercontent.com/u/11502284?v=4\",\n" +
                "                    \"gravatar_id\": \"\",\n" +
                "                    \"url\": \"https://api.github.com/users/pburls\",\n" +
                "                    \"html_url\": \"https://github.com/pburls\",\n" +
                "                    \"followers_url\": \"https://api.github.com/users/pburls/followers\",\n" +
                "                    \"following_url\": \"https://api.github.com/users/pburls/following{/other_user}\",\n" +
                "                    \"gists_url\": \"https://api.github.com/users/pburls/gists{/gist_id}\",\n" +
                "                    \"starred_url\": \"https://api.github.com/users/pburls/starred{/owner}{/repo}\",\n" +
                "                    \"subscriptions_url\": \"https://api.github.com/users/pburls/subscriptions\",\n" +
                "                    \"organizations_url\": \"https://api.github.com/users/pburls/orgs\",\n" +
                "                    \"repos_url\": \"https://api.github.com/users/pburls/repos\",\n" +
                "                    \"events_url\": \"https://api.github.com/users/pburls/events{/privacy}\",\n" +
                "                    \"received_events_url\": \"https://api.github.com/users/pburls/received_events\",\n" +
                "                    \"type\": \"User\",\n" +
                "                    \"site_admin\": false\n" +
                "                },\n" +
                "                \"html_url\": \"https://github.com/pburls/specs-app\",\n" +
                "                \"description\": null,\n" +
                "                \"fork\": false,\n" +
                "                \"url\": \"https://api.github.com/repos/pburls/specs-app\",\n" +
                "                \"forks_url\": \"https://api.github.com/repos/pburls/specs-app/forks\",\n" +
                "                \"keys_url\": \"https://api.github.com/repos/pburls/specs-app/keys{/key_id}\",\n" +
                "                \"collaborators_url\": \"https://api.github.com/repos/pburls/specs-app/collaborators{/collaborator}\",\n" +
                "                \"teams_url\": \"https://api.github.com/repos/pburls/specs-app/teams\",\n" +
                "                \"hooks_url\": \"https://api.github.com/repos/pburls/specs-app/hooks\",\n" +
                "                \"issue_events_url\": \"https://api.github.com/repos/pburls/specs-app/issues/events{/number}\",\n" +
                "                \"events_url\": \"https://api.github.com/repos/pburls/specs-app/events\",\n" +
                "                \"assignees_url\": \"https://api.github.com/repos/pburls/specs-app/assignees{/user}\",\n" +
                "                \"branches_url\": \"https://api.github.com/repos/pburls/specs-app/branches{/branch}\",\n" +
                "                \"tags_url\": \"https://api.github.com/repos/pburls/specs-app/tags\",\n" +
                "                \"blobs_url\": \"https://api.github.com/repos/pburls/specs-app/git/blobs{/sha}\",\n" +
                "                \"git_tags_url\": \"https://api.github.com/repos/pburls/specs-app/git/tags{/sha}\",\n" +
                "                \"git_refs_url\": \"https://api.github.com/repos/pburls/specs-app/git/refs{/sha}\",\n" +
                "                \"trees_url\": \"https://api.github.com/repos/pburls/specs-app/git/trees{/sha}\",\n" +
                "                \"statuses_url\": \"https://api.github.com/repos/pburls/specs-app/statuses/{sha}\",\n" +
                "                \"languages_url\": \"https://api.github.com/repos/pburls/specs-app/languages\",\n" +
                "                \"stargazers_url\": \"https://api.github.com/repos/pburls/specs-app/stargazers\",\n" +
                "                \"contributors_url\": \"https://api.github.com/repos/pburls/specs-app/contributors\",\n" +
                "                \"subscribers_url\": \"https://api.github.com/repos/pburls/specs-app/subscribers\",\n" +
                "                \"subscription_url\": \"https://api.github.com/repos/pburls/specs-app/subscription\",\n" +
                "                \"commits_url\": \"https://api.github.com/repos/pburls/specs-app/commits{/sha}\",\n" +
                "                \"git_commits_url\": \"https://api.github.com/repos/pburls/specs-app/git/commits{/sha}\",\n" +
                "                \"comments_url\": \"https://api.github.com/repos/pburls/specs-app/comments{/number}\",\n" +
                "                \"issue_comment_url\": \"https://api.github.com/repos/pburls/specs-app/issues/comments{/number}\",\n" +
                "                \"contents_url\": \"https://api.github.com/repos/pburls/specs-app/contents/{+path}\",\n" +
                "                \"compare_url\": \"https://api.github.com/repos/pburls/specs-app/compare/{base}...{head}\",\n" +
                "                \"merges_url\": \"https://api.github.com/repos/pburls/specs-app/merges\",\n" +
                "                \"archive_url\": \"https://api.github.com/repos/pburls/specs-app/{archive_format}{/ref}\",\n" +
                "                \"downloads_url\": \"https://api.github.com/repos/pburls/specs-app/downloads\",\n" +
                "                \"issues_url\": \"https://api.github.com/repos/pburls/specs-app/issues{/number}\",\n" +
                "                \"pulls_url\": \"https://api.github.com/repos/pburls/specs-app/pulls{/number}\",\n" +
                "                \"milestones_url\": \"https://api.github.com/repos/pburls/specs-app/milestones{/number}\",\n" +
                "                \"notifications_url\": \"https://api.github.com/repos/pburls/specs-app/notifications{?since,all,participating}\",\n" +
                "                \"labels_url\": \"https://api.github.com/repos/pburls/specs-app/labels{/name}\",\n" +
                "                \"releases_url\": \"https://api.github.com/repos/pburls/specs-app/releases{/id}\",\n" +
                "                \"deployments_url\": \"https://api.github.com/repos/pburls/specs-app/deployments\"\n" +
                "            },\n" +
                "            \"score\": 38.077095\n" +
                "        }\n" +
                "    ]\n" +
                "}"
        and: "the app installation authentication header interceptor to be used for the request"
        1 * appInstallationAuthenticationHeaderRequestInterceptor.intercept(_,_,_) >> { HttpRequest request, byte[] body, ClientHttpRequestExecution execution ->
            execution.execute(request, body)
        }

        expect: "the github search code api endpoint to be called with a get"
        this.server.expect(requestTo("/search/code?q=filename:"+filename))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseContent, MediaType.APPLICATION_JSON));

        and: "the search results to be returned"
        def searchCodeResults = client.findFiles(filename, null, null, null)
        searchCodeResults
        !searchCodeResults.isIncompleteResults()
        searchCodeResults.getItems()
        searchCodeResults.getItems()[0].getName() == filename
        searchCodeResults.getItems()[0].getRepository().getFull_name() == "pburls/specs-app"
        searchCodeResults.getItems()[0].getRepository().getHtml_url() == "https://github.com/pburls/specs-app"
    }
}
