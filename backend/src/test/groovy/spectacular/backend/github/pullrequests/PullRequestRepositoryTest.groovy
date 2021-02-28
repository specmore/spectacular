package spectacular.backend.github.pullrequests

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.graphql.ChangedFile
import spectacular.backend.github.graphql.Connection
import spectacular.backend.github.graphql.GraphQlResponse
import spectacular.backend.github.graphql.Repository
import spectacular.backend.github.graphql.RepositoryRef
import spectacular.backend.github.graphql.RepositoryWithPullRequests
import spectacular.backend.github.graphql.ResponseData
import spock.lang.Specification

import java.time.OffsetDateTime

class PullRequestRepositoryTest extends Specification {
    def restApiClient = Mock(RestApiClient)
    def pullRequestRepository = new PullRequestRepository(restApiClient)

    def "GetPullRequestsForRepo ignores pull requests from unknown branches"() {
        given: "A repository"
        def graphQlRepo = new Repository("test-owner/test-repo", new URI("some-url"))
        def repo = RepositoryId.createRepositoryFrom(graphQlRepo)

        and: "a Pull Request from a valid branch"
        def validRef = new RepositoryRef("a-valid-branch-name", graphQlRepo, null, null)
        def labels = new Connection(0, [])
        def changedFiles = new Connection(1, [new ChangedFile("test-changed-file")])
        def validPullRequest = new spectacular.backend.github.graphql.PullRequest(99, new URI("test-url"), labels, changedFiles, "valid PR title", OffsetDateTime.now(), validRef)

        and: "a Pull Request from an unknown branch"
        def unknownRef = null
        def unknownPullRequest = new spectacular.backend.github.graphql.PullRequest(101, new URI("test-url2"), labels, changedFiles, "unknown branch PR title", OffsetDateTime.now(), unknownRef)

        and: "the Pull Requests belong to the repository"
        def pullRequestsConnection = new Connection(2, [validPullRequest, unknownPullRequest])
        def repositoryWithPullRequests = new RepositoryWithPullRequests(graphQlRepo.getNameWithOwner(), graphQlRepo.getUrl(), pullRequestsConnection, null)
        def graphQlResponseData = new ResponseData(repositoryWithPullRequests)
        def graphQlReponse = new GraphQlResponse(graphQlResponseData, JsonNodeFactory.instance.arrayNode())

        when: "the Pull Requests are retrieved for changed file"
        def pullRequestsResult = pullRequestRepository.getPullRequestsForRepoAndFile(repo, "test-changed-file")

        then: "the Pull Requests should have been queried from the GitHub GraphQL API for the repository"
        1 * restApiClient.graphQlQuery(_) >> graphQlReponse

        and: "only the valid Pull Request should be returned"
        pullRequestsResult.size() == 1
        pullRequestsResult.first().getBranchName() == "a-valid-branch-name"
    }
}
