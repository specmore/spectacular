package spectacular.backend.pullrequests

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import spectacular.backend.common.Repository
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.graphql.Connection
import spectacular.backend.github.graphql.GraphQlResponse
import spectacular.backend.github.graphql.Ref
import spectacular.backend.github.graphql.RepositoryWithPullRequests
import spectacular.backend.github.graphql.ResponseData
import spock.lang.Specification

import java.time.Instant

class PullRequestServiceTest extends Specification {
    def restApiClient = Mock(RestApiClient)
    def pullRequestService = new PullRequestService(restApiClient)

    def "GetPullRequestsForRepo ignores pull requests from unknown branches"() {
        given: "A repository"
        def graphQlRepo = new spectacular.backend.github.graphql.Repository("test-owner/test-repo", "some-url")
        def repo = Repository.createRepositoryFrom(graphQlRepo)

        and: "a Pull Request from a valid branch"
        def validRef = new Ref("a-valid-branch-name", graphQlRepo)
        def labels = new Connection(0, [])
        def changedFiles = new Connection(0, [])
        def validPullRequest = new spectacular.backend.github.graphql.PullRequest(99, "test-url", labels, changedFiles, "valid PR title", Instant.now(), validRef)

        and: "a Pull Request from an unknown branch"
        def unknownRef = null
        def unknownPullRequest = new spectacular.backend.github.graphql.PullRequest(101, "test-url2", labels, changedFiles, "unknown branch PR title", Instant.now(), unknownRef)

        and: "the Pull Requests belong to the repository"
        def pullRequestsConnection = new Connection(2, [validPullRequest, unknownPullRequest])
        def repositoryWithPullRequests = new RepositoryWithPullRequests(graphQlRepo.getNameWithOwner(), graphQlRepo.getUrl(), pullRequestsConnection)
        def graphQlResponseData = new ResponseData(repositoryWithPullRequests)
        def graphQlReponse = new GraphQlResponse(graphQlResponseData, JsonNodeFactory.instance.arrayNode())

        when: "the Pull Requests are retrieved"
        def pullRequestsResult = pullRequestService.getPullRequestsForRepo(repo)

        then: "the Pull Requests should have been queried from the GitHub GraphQL API for the repository"
        1 * restApiClient.graphQlQuery(_) >> graphQlReponse

        and: "only the valid Pull Request should be returned"
        pullRequestsResult.size() == 1
        pullRequestsResult.first().getBranchName() == "a-valid-branch-name"
    }
}
