package spectacular.backend.specevolution


import spectacular.backend.common.RepositoryId
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.domain.Comparison
import spectacular.backend.github.pullrequests.PullRequest
import spectacular.backend.github.refs.BranchRef
import spectacular.backend.github.refs.TagRef
import spock.lang.Specification

import java.time.OffsetDateTime

class EvolutionBranchBuilderTest extends Specification {
    def restApiClient = Mock(RestApiClient)
    def evolutionBranchBuilder = new EvolutionBranchBuilder(restApiClient)

    def "GenerateEvolutionItems an evolution item for the head commit of the branch"() {
        given: "a spec file repository and branch"
        def specFileRepoId = RepositoryId.createForNameWithOwner("test-owner/test-repo")
        def branch = new BranchRef("test-branch", "some file contents", "1234asdf5678")

        when: "generating the evolution items for the branch"
        def evolutionItems = evolutionBranchBuilder.generateEvolutionItems(specFileRepoId, branch, [], [])

        then: "a spec evolution item for the head commit is returned"
        evolutionItems.size() == 1
        evolutionItems.first().getRef() == branch.getName()
        evolutionItems.first().getBranchName() == branch.getName()
    }

    def "GenerateEvolutionItems only returns evolution items for tags behind or on the branch head"() {
        given: "a spec file repository and branch"
        def specFileRepoId = RepositoryId.createForNameWithOwner("test-owner/test-repo")
        def branch = new BranchRef("test-branch", "", "1234asdf5678")

        and: "a tag behind the branch head on the repository"
        def behindTag = new TagRef("behindTag", "behindCommit")
        def behindTagComparison = new Comparison(null, "behind", 0, 1, 1)

        and: "a tag ahead of the branch head on the repository"
        def aheadTag = new TagRef("aheadTag", "aheadCommit")
        def aheadTagComparison = new Comparison(null, "ahead", 1, 0, 1)

        and: "a list of the tags extracted"
        def tagList = [behindTag, aheadTag]

        when: "generating the evolution items for the branch"
        def evolutionItems = evolutionBranchBuilder.generateEvolutionItems(specFileRepoId, branch, tagList, [])

        then: "both tags are compared to the branch"
        1 * restApiClient.getComparison(specFileRepoId, branch.getName(), behindTag.getName()) >> behindTagComparison
        1 * restApiClient.getComparison(specFileRepoId, branch.getName(), aheadTag.getName()) >> aheadTagComparison

        and: "a spec evolution item for the head commit is first returned"
        evolutionItems.first().getRef() == branch.getName()
        evolutionItems.first().getBranchName() == branch.getName()

        and: "a only another spec evolution item for the behind tag"
        evolutionItems.size() == 2
        evolutionItems[1].getTag() == "behindTag"
        evolutionItems[1].getRef() == behindTag.getName()
    }

    def "GenerateEvolutionItems returns evolution items for PRs before the branch head"() {
        given: "a spec file repository and release branch"
        def specFileRepoId = RepositoryId.createForNameWithOwner("test-owner/test-repo")
        def releaseBranch = new BranchRef("release-branch-1", "", "branchHead")

        and: "an open pull request on the repository"
        def prBranch = "feature-branch-1"
        def pullRequest = new PullRequest(specFileRepoId, prBranch, releaseBranch.getName(), 99, new URI("https://test-url"), [], [], "test-pr", OffsetDateTime.now())

        when: "generating the evolution items for the branch"
        def evolutionItems = evolutionBranchBuilder.generateEvolutionItems(specFileRepoId, releaseBranch, [], [pullRequest])

        then: "2 spec evolution items are returned"
        evolutionItems.size() == 2

        and: "the first item is the for the PR"
        evolutionItems[0].getPullRequest()
        evolutionItems[0].getRef() == prBranch

        and: "the second item is the for the branch head"
        evolutionItems[1].getRef() == releaseBranch.getName()
        evolutionItems[1].getBranchName() == releaseBranch.getName()
    }
}
