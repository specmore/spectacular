package spectacular.backend.specevolution


import spectacular.backend.common.RepositoryId
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.domain.Comparison
import spectacular.backend.github.domain.Tag
import spectacular.backend.github.pullrequests.PullRequest
import spock.lang.Specification

import java.time.OffsetDateTime

class EvolutionBranchBuilderTest extends Specification {
    def restApiClient = Mock(RestApiClient)
    def evolutionBranchBuilder = new EvolutionBranchBuilder(restApiClient)

    def "GenerateEvolutionItems only returns evolution items for tags behind the branch"() {
        given: "a spec file repository and branch"
        def specFileRepoId = RepositoryId.createForNameWithOwner("test-owner/test-repo")
        def branchName = "test-branch"

        and: "a tag behind the branch head on the repository"
        def behindTag = new Tag("behindTag")
        def behindTagComparison = new Comparison(null, "behind", 0, 1, 1)

        and: "a tag ahead of the branch head on the repository"
        def aheadTag = new Tag("aheadTag")
        def aheadTagComparison = new Comparison(null, "ahead", 1, 0, 1)

        and: "a list of the tags extracted"
        def tagList = [behindTag, aheadTag]

        when: "generating the evolution items for the branch"
        def evolutionItems = evolutionBranchBuilder.generateEvolutionItems(specFileRepoId, branchName, tagList, [])

        then: "both tags are compared to the branch"
        1 * restApiClient.getComparison(specFileRepoId, branchName, behindTag.getName()) >> behindTagComparison
        1 * restApiClient.getComparison(specFileRepoId, branchName, aheadTag.getName()) >> aheadTagComparison

        and: "the spec evolution items returned only contains the behind tag"
        evolutionItems.size() == 1
        evolutionItems.first().tag == "behindTag"
    }

    def "GenerateEvolutionItems returns evolution items for PRs before tags"() {
        given: "a spec file repository and release branch"
        def specFileRepoId = RepositoryId.createForNameWithOwner("test-owner/test-repo")
        def releaseBranchName = "release-branch-1"

        and: "an open pull request on the repository"
        def prBranch = "feature-branch-1"
        def pullRequest = new PullRequest(specFileRepoId, prBranch, releaseBranchName, 99, new URI("https://test-url"), [], [], "test-pr", OffsetDateTime.now())

        and: "a tag behind the branch head on the repository"
        def tag = new Tag("behindTag")
        def tagComparison = new Comparison(null, "behind", 0, 1, 1)

        when: "generating the evolution items for the branch"
        def evolutionItems = evolutionBranchBuilder.generateEvolutionItems(specFileRepoId, releaseBranchName, [tag], [pullRequest])

        then: "tags are compared to the branch"
        1 * restApiClient.getComparison(specFileRepoId, releaseBranchName, tag.getName()) >> tagComparison

        and: "2 spec evolution items are returned"
        evolutionItems.size() == 2

        and: "the first item is the for the PR"
        evolutionItems[0].getPullRequest()

        and: "the second item is the for the tag"
        evolutionItems[1].getTag()
    }
}
