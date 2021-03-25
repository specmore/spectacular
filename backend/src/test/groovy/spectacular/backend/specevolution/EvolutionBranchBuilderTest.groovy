package spectacular.backend.specevolution


import spectacular.backend.common.RepositoryId
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.domain.Comparison
import spectacular.backend.github.pullrequests.PullRequest
import spectacular.backend.github.refs.BranchRef
import spectacular.backend.github.refs.TagRef
import spectacular.backend.specs.SpecService
import spock.lang.Specification

import java.time.OffsetDateTime

class EvolutionBranchBuilderTest extends Specification {
    def restApiClient = Mock(RestApiClient)
    def specService = Mock(SpecService)
    def evolutionBranchBuilder = new EvolutionBranchBuilder(restApiClient, specService)

    def specFileRepoId = RepositoryId.createForNameWithOwner("test-owner/test-repo")
    def specFilePath = "spec-file.yaml"

    def "GenerateEvolutionItems an evolution item for the head commit of the branch"() {
        given: "a spec file repository and branch"
        def branch = new BranchRef("test-branch", "some file contents", "1234asdf5678")

        when: "generating the evolution items for the branch"
        def evolutionItems = evolutionBranchBuilder.generateEvolutionItems(specFileRepoId, specFilePath, branch, [], [])

        then: "a spec evolution item for the head commit is returned"
        evolutionItems.size() == 1
        evolutionItems.first().getRef() == branch.getName()
        evolutionItems.first().getBranchName() == branch.getName()
    }

    def "GenerateEvolutionItems only returns evolution items for tags behind or on the branch head"() {
        given: "a spec file repository and branch"
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
        def evolutionItems = evolutionBranchBuilder.generateEvolutionItems(specFileRepoId, specFilePath, branch, tagList, [])

        then: "both tags are compared to the branch"
        1 * restApiClient.getComparison(specFileRepoId, branch.getName(), behindTag.getName()) >> behindTagComparison
        1 * restApiClient.getComparison(specFileRepoId, branch.getName(), aheadTag.getName()) >> aheadTagComparison

        and: "a spec evolution item for the head commit is first returned"
        evolutionItems.first().getRef() == branch.getName()
        evolutionItems.first().getBranchName() == branch.getName()

        and: "a only another spec evolution item for the behind tag"
        evolutionItems.size() == 2
        evolutionItems[1].getTags() == ["behindTag"]
        evolutionItems[1].getRef() == behindTag.getName()
    }

    def "GenerateEvolutionItems returns one evolution item for tags behind the same number of commits"() {
        given: "a spec file repository and branch"
        def branch = new BranchRef("test-branch", "", "1234asdf5678")

        and: "a tag behind the branch head on the repository by 2 commits"
        def behindTag = new TagRef("behindTag", "behind2Commits")
        def behindTagComparison = new Comparison(null, "behind", 0, 2, 2)

        and: "another tag behind the branch head on the repository also by 2 commits"
        def behindTag2 = new TagRef("behindTag2", "behind2Commits")
        def behindTag2Comparison = new Comparison(null, "behind", 0, 2, 2)

        and: "a list of the tags extracted"
        def tagList = [behindTag, behindTag2]

        when: "generating the evolution items for the branch"
        def evolutionItems = evolutionBranchBuilder.generateEvolutionItems(specFileRepoId, specFilePath, branch, tagList, [])

        then: "both tags are compared to the branch"
        1 * restApiClient.getComparison(specFileRepoId, branch.getName(), behindTag.getName()) >> behindTagComparison
        1 * restApiClient.getComparison(specFileRepoId, branch.getName(), behindTag2.getName()) >> behindTag2Comparison

        and: "beside the branch head item, only another spec evolution item for the behind tag"
        evolutionItems.size() == 2
        evolutionItems[1].getRef() == behindTag.getCommit()
        evolutionItems[1].getTags() == [behindTag.getName(), behindTag2.getName()]
    }

    def "GenerateEvolutionItems only returns only one evolution items for tags on the branch head"() {
        given: "a spec file repository and branch"
        def branch = new BranchRef("test-branch", "", "headCommit")

        and: "a tag on the branch head"
        def onHeadTag = new TagRef("onHeadTag", "headCommit")

        and: "a list of the tags extracted"
        def tagList = [onHeadTag]

        when: "generating the evolution items for the branch"
        def evolutionItems = evolutionBranchBuilder.generateEvolutionItems(specFileRepoId, specFilePath, branch, tagList, [])

        then: "only a spec evolution item for the head commit is returned"
        evolutionItems.size() == 1

        and: "it has the branch name"
        evolutionItems.first().getRef() == branch.getName()
        evolutionItems.first().getBranchName() == branch.getName()

        and: "it has the branch name"
        evolutionItems.first().getTags() == [onHeadTag.getName()]
    }

    def "GenerateEvolutionItems returns evolution items for PRs before the branch head"() {
        given: "a spec file repository and release branch"
        def releaseBranch = new BranchRef("release-branch-1", "", "branchHead")

        and: "an open pull request on the repository"
        def prBranch = "feature-branch-1"
        def pullRequest = new PullRequest(specFileRepoId, prBranch, releaseBranch.getName(), 99, new URI("https://test-url"), [], [], "test-pr", OffsetDateTime.now())

        when: "generating the evolution items for the branch"
        def evolutionItems = evolutionBranchBuilder.generateEvolutionItems(specFileRepoId, specFilePath, releaseBranch, [], [pullRequest])

        then: "2 spec evolution items are returned"
        evolutionItems.size() == 2

        and: "the first item is the for the PR"
        evolutionItems[0].getPullRequest()
        evolutionItems[0].getRef() == prBranch

        and: "has an empty tags list"
        evolutionItems[0].getTags().isEmpty()

        and: "the second item is the for the branch head"
        evolutionItems[1].getRef() == releaseBranch.getName()
        evolutionItems[1].getBranchName() == releaseBranch.getName()
    }

    def "GenerateEvolutionItems gets spec item information for each evolution item"() {
        given: "a spec file repository and branch"
        def branchName = "test-branch"
        def branch = new BranchRef(branchName, "", "1234asdf5678")

        and: "a tag behind the branch head on the repository"
        def tagName = "behindTag"
        def behindTag = new TagRef(tagName, "behindCommit")
        def behindTagComparison = new Comparison(null, "behind", 0, 1, 1)
        restApiClient.getComparison(specFileRepoId, branch.getName(), behindTag.getName()) >> behindTagComparison

        and: "an open pull request on the repository"
        def prBranch = "feature-branch-1"
        def pullRequest = new PullRequest(specFileRepoId, prBranch, branch.getName(), 99, new URI("https://test-url"), [], [], "test-pr", OffsetDateTime.now())

        when: "generating the evolution items for the branch"
        evolutionBranchBuilder.generateEvolutionItems(specFileRepoId, specFilePath, branch, [behindTag], [pullRequest])

        then: "the spec item for the branch is retrieved"
        1 * specService.getSpecItem(specFileRepoId, specFilePath, branchName)

        and: "the spec item for the tag is retrieved"
        1 * specService.getSpecItem(specFileRepoId, specFilePath, tagName)

        and: "the spec item for the pr is retrieved"
        1 * specService.getSpecItem(specFileRepoId, specFilePath, prBranch)
    }
}
