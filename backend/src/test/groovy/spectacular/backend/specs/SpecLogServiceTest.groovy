package spectacular.backend.specs

import spectacular.backend.common.Repository
import spectacular.backend.pullrequests.PullRequest
import spock.lang.Specification

import java.time.Instant

class SpecLogServiceTest extends Specification {
    def specService = Mock(SpecService)
    def specLogService = new SpecLogService(specService)

    def "getSpecLogForSpecRepoAndFile returns a spec item from master as the latest agreed spec item"() {
        given: "a catalogue spec file repo and path"
        def specFileRepo = new Repository("test-owner", "spec-repo")
        def specFilePath = "test-specs/example-spec.yaml"

        and: "a spec item on the master branch"
        def masterBranchSpecItem = new SpecItem(specFileRepo, specFilePath, null, "xyz", null, null, null)

        when: "the spec log is retrieved"
        def specLogResult = specLogService.getSpecLogForSpecRepoAndFile(specFileRepo, specFilePath, [])

        then: "the spec item on the master branch is retrieved"
        1 * specService.getSpecItem(specFileRepo, specFilePath, "master") >> masterBranchSpecItem

        and: "a valid spec log returned has the master branch spec item set as the latest agreed spec item"
        specLogResult
        specLogResult.getId() == "test-owner/spec-repo/test-specs/example-spec.yaml"
        specLogResult.getLatestAgreed() == masterBranchSpecItem
    }

    def "getSpecLogForSpecRepoAndFile returns a spec change proposal an open pull request that changed the spec file"() {
        given: "a catalogue spec file repo and path"
        def specFileRepo = new Repository("test-owner", "spec-repo")
        def specFilePath = "test-specs/example-spec.yaml"

        and: "a spec item on the master branch"
        def masterBranchSpecItem = new SpecItem(specFileRepo, specFilePath, null, "xyz", null, null, null)

        and: "an open pull request that changed the spec file in another branch"
        def changeBranch = "test-branch"
        def openPullRequest = new PullRequest(specFileRepo, changeBranch, 99, "test-url", [], [specFilePath], "test-pr", Instant.now())
        def changedSpecItem = new SpecItem(specFileRepo, specFilePath, null, changeBranch, null, null, null)

        when: "the spec log is retrieved"
        def specLogResult = specLogService.getSpecLogForSpecRepoAndFile(specFileRepo, specFilePath, [openPullRequest])

        then: "the spec item on the master branch is retrieved"
        1 * specService.getSpecItem(specFileRepo, specFilePath, "master") >> masterBranchSpecItem

        and: "the spec item is retrieved from the branch of the pull request"
        1 * specService.getSpecItem(specFileRepo, specFilePath, changeBranch) >> changedSpecItem

        and: "a valid spec log is returned"
        specLogResult

        and: "contains a proposal for the open pull request with the changed spec item"
        specLogResult.getProposedChanges().size() == 1
        specLogResult.getProposedChanges()[0].getId() == 99
        specLogResult.getProposedChanges()[0].getPullRequest() == openPullRequest
        specLogResult.getProposedChanges()[0].getSpecItem() == changedSpecItem
    }
}
