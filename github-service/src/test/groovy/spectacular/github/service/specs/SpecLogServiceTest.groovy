package spectacular.github.service.specs

import spectacular.github.service.common.Repository
import spock.lang.Specification

class SpecLogServiceTest extends Specification {
    def specService = Mock(SpecService)
    def specLogService = new SpecLogService(specService)

    def "getSpecLogForSpecRepoAndFile always returns spec log with latest agree spec item"() {
        given: "a known spec file repo and path"
        def specFileRepo = new Repository("test-owner", "spec-repo");
        def specFilePath = "test-specs/example-spec.yaml"

        and: "a latest agreed spec item on the master branch"
        def latestAgreedSpecItem = Mock(SpecItem)

        when: "the spec evolution is retrieved"
        def specEvolutionResult = specLogService.getSpecLogForSpecRepoAndFile(specFileRepo, specFilePath)

        then: "the spec item for the master branch is retrieved"
        1 * specService.getSpecItem(specFileRepo, specFilePath, "master") >> latestAgreedSpecItem

        and: "a valid spec evolution is returned with the latest agreed spec item"
        specEvolutionResult
        specEvolutionResult.getLatestAgreed() == latestAgreedSpecItem
    }
}
