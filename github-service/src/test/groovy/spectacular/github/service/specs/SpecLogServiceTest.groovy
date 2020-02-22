package spectacular.github.service.specs

import spectacular.github.service.common.Repository
import spectacular.github.service.pullrequests.PullRequest
import spock.lang.Specification

class SpecLogServiceTest extends Specification {
    def specService = Mock(SpecService)
    def specLogService = new SpecLogService(specService)

    def "getSpecLogForSpecRepoAndFile returns a spec item from master as the latest agreed spec item"() {
        given: "a catalogue spec file repo and path"
        def specFileRepo = new Repository("test-owner", "spec-repo")
        def specFilePath = "test-specs/example-spec.yaml"

        and: "a spec item on the master branch"
        def masterBranchSpecItem = Mock(SpecItem)

        when: "the spec log is retrieved"
        def specLogResult = specLogService.getSpecLogForSpecRepoAndFile(specFileRepo, specFilePath, [])

        then: "the spec item on the master branch is retrieved"
        1 * specService.getSpecItem(specFileRepo, specFilePath, "master") >> masterBranchSpecItem

        and: "a valid spec log returned has the master branch spec item set as the latest agreed spec item"
        specLogResult
        specLogResult.getLatestAgreed() == masterBranchSpecItem
    }

    def "getSpecLogForSpecRepoAndFile returns a spec change proposal an open pull request that changed the spec file"() {
        given: "a catalogue spec file repo and path"
        def specFileRepo = new Repository("test-owner", "spec-repo")
        def specFilePath = "test-specs/example-spec.yaml"

        and: "an open pull request that changed the spec file in another branch"
        def changeBranch = "test-branch"
        def openPullRequest = new PullRequest(specFileRepo, changeBranch, 1, "test-url", [], [specFilePath], "test-pr")
        def changedSpecItem = Mock(SpecItem)

        when: "the spec log is retrieved"
        def specLogResult = specLogService.getSpecLogForSpecRepoAndFile(specFileRepo, specFilePath, [openPullRequest])

        then: "the spec item is retrieved from the branch of the pull request"
        1 * specService.getSpecItem(specFileRepo, specFilePath, changeBranch) >> changedSpecItem

        and: "a valid spec log is returned"
        specLogResult

        and: "contains a proposal for the open pull request with the changed spec item"
        specLogResult.getProposedChanges().size() == 1
        specLogResult.getProposedChanges()[0].getPullRequest() == openPullRequest
        specLogResult.getProposedChanges()[0].getSpecItem() == changedSpecItem
    }
}
