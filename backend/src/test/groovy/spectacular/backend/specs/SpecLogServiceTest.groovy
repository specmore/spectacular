package spectacular.backend.specs

import spectacular.backend.cataloguemanifest.model.Catalogue
import spectacular.backend.cataloguemanifest.model.Interface
import spectacular.backend.cataloguemanifest.model.Interfaces
import spectacular.backend.cataloguemanifest.model.SpecFileLocation
import spectacular.backend.common.CatalogueId
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.pullrequests.PullRequest
import spectacular.backend.github.pullrequests.PullRequestRepository
import spock.lang.Specification

import java.time.OffsetDateTime

class SpecLogServiceTest extends Specification {
    def specService = Mock(SpecService)
    def pullRequestRepository = Mock(PullRequestRepository)
    def specLogService = new SpecLogService(specService, pullRequestRepository)

    def generateTestCatalogueId() {
        def catalogueRepository = RepositoryId.createForNameWithOwner("test-owner/test-catalogue-repo")
        def catalogueFilePath = "test-file.yml"
        def catalogueName = "testCatalogue"
        return new CatalogueId(catalogueRepository, catalogueFilePath, catalogueName)
    }

    def generateTestInterface(String specFilePath, RepositoryId specFileRepository) {
        def _interface = new Interface();
        def specFileLocation = new SpecFileLocation().withFilePath(specFilePath)
        if(specFileRepository) {
            specFileLocation.setRepo(specFileRepository.getNameWithOwner())
        }
        return _interface.withSpecFile(specFileLocation)
    }

    def "getSpecLogsFor returns a SpecLog for interface in catalogue without repo specified"() {
        given: "a catalogue from a catalogue manifest"
        def catalogue = new Catalogue();
        def catalogueId = generateTestCatalogueId();

        and: "with an interface and spec file location set with only a file path"
        def specFilePath = "test-spec-file-path";
        def _interface = generateTestInterface(specFilePath, null)
        catalogue.setInterfaces(new Interfaces().withAdditionalProperty("testInterface1", _interface));

        and: "a spec item at the file path on the master branch"
        def masterBranchSpecItem = Mock(spectacular.backend.api.model.SpecItem)

        and: "a single open pull request changing the spec file"
        def prBranch = "test-branch"
        def openPullRequest = new PullRequest(catalogueId.getRepositoryId(), prBranch, 99, new URI("https://test-url"), [], [specFilePath], "test-pr", OffsetDateTime.now())

        and: "a spec item at the file path on the open pull request's source branch"
        def prBranchSpecItem = Mock(spectacular.backend.api.model.SpecItem)

        when: "the specLogs for the catalogue are retrieved"
        def specLogs = specLogService.getSpecLogsFor(catalogue, catalogueId)

        then: "a specLog item is return for the interface"
        specLogs
        specLogs.size() == 1
        def specLog = specLogs.first()
        specLog.getInterfaceName() == "testInterface1"

        and: "a spec item on the master branch is retrieved for the spec file location in the catalogue's repo"
        1 * specService.getSpecItem(catalogueId.getRepositoryId(), specFilePath, "master") >> masterBranchSpecItem

        and: "the master branch spec item is set as the latest agreed item on the spec log"
        specLog.getLatestAgreed() == masterBranchSpecItem

        and: "the open pull request is retrieved for changing the spec file in the catalogue's repo"
        1 * pullRequestRepository.getPullRequestsForRepoAndFile(catalogueId.getRepositoryId(), "test-spec-file-path") >> [openPullRequest]

        and: "a single proposed change is returned on the specLog item for the open pull request"
        specLog.getProposedChanges()
        specLog.getProposedChanges().size() == 1
        def proposedChange = specLog.getProposedChanges().first()
        proposedChange.getId() == openPullRequest.getNumber()
        proposedChange.getPullRequest().getNumber() == openPullRequest.getNumber()

        and: "a spec item on the pull request's source branch is retrieved for the spec file location in the catalogue's repo"
        1 * specService.getSpecItem(catalogueId.getRepositoryId(), specFilePath, prBranch) >> prBranchSpecItem

        and: "the pull request's source branch spec item is set as the spec item for the proposed change"
        proposedChange.getSpecItem() == prBranchSpecItem
    }

    def "getSpecLogsFor returns a SpecLog for interface in catalogue with repo specified"() {
        given: "a catalogue from a catalogue manifest"
        def catalogue = new Catalogue();
        def catalogueId = generateTestCatalogueId();

        and: "with an interface and spec file location set with a file path and repository"
        def specFilePath = "test-spec-file-path";
        def specFileRepository = RepositoryId.createForNameWithOwner("test-owner/test-catalogue-repo")
        def _interface = generateTestInterface(specFilePath, specFileRepository)
        catalogue.setInterfaces(new Interfaces().withAdditionalProperty("testInterface1", _interface));

        and: "a spec item at the file path on the master branch"
        def masterBranchSpecItem = Mock(spectacular.backend.api.model.SpecItem)

        and: "a single open pull request changing the spec file"
        def prBranch = "test-branch"
        def openPullRequest = new PullRequest(specFileRepository, prBranch, 99, new URI("https://test-url"), [], [specFilePath], "test-pr", OffsetDateTime.now())

        and: "a spec item at the file path on the open pull request's source branch"
        def prBranchSpecItem = Mock(spectacular.backend.api.model.SpecItem)

        when: "the specLogs for the catalogue are retrieved"
        def specLogs = specLogService.getSpecLogsFor(catalogue, catalogueId)

        then: "a specLog item is return for the interface"
        specLogs
        specLogs.size() == 1
        def specLog = specLogs.first()
        specLog.getInterfaceName() == "testInterface1"

        and: "a spec item on the master branch is retrieved for the spec file location in the spec file location's repo"
        1 * specService.getSpecItem(specFileRepository, specFilePath, "master") >> masterBranchSpecItem

        and: "the master branch spec item is set as the latest agreed item on the spec log"
        specLog.getLatestAgreed() == masterBranchSpecItem

        and: "the open pull request is retrieved for changing the spec file in the spec file location's repo"
        1 * pullRequestRepository.getPullRequestsForRepoAndFile(specFileRepository, "test-spec-file-path") >> [openPullRequest]

        and: "a single proposed change is returned on the specLog item for the open pull request"
        specLog.getProposedChanges()
        specLog.getProposedChanges().size() == 1
        def proposedChange = specLog.getProposedChanges().first()
        proposedChange.getId() == openPullRequest.getNumber()
        proposedChange.getPullRequest().getNumber() == openPullRequest.getNumber()

        and: "a spec item on the pull request's source branch is retrieved for the spec file location in the spec file location's repo"
        1 * specService.getSpecItem(specFileRepository, specFilePath, prBranch) >> prBranchSpecItem

        and: "the pull request's source branch spec item is set as the spec item for the proposed change"
        proposedChange.getSpecItem() == prBranchSpecItem
    }
}
