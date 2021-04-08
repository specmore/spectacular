package spectacular.backend.interfaces

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spectacular.backend.api.model.SpecEvolution
import spectacular.backend.api.model.SpecEvolutionSummary
import spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemErrorType
import spectacular.backend.cataloguemanifest.model.Interface
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig
import spectacular.backend.cataloguemanifest.model.SpecFileLocation
import spectacular.backend.catalogues.CatalogueService
import spectacular.backend.common.CatalogueId
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.domain.ContentItem
import spectacular.backend.specevolution.SpecEvolutionService
import spectacular.backend.specevolution.SpecEvolutionSummaryMapper
import spock.lang.Specification

class InterfaceServiceTest extends Specification {
    def restApiClient = Mock(RestApiClient)
    def specEvolutionService = Mock(SpecEvolutionService)
    def specEvolutionSummaryMapper = Mock(SpecEvolutionSummaryMapper)
    def interfaceService = new InterfaceService(restApiClient, specEvolutionService, specEvolutionSummaryMapper)

    def aCatalogue() {
        def catalogueRepo = new RepositoryId("test-owner","test-repo987")
        def catalogueManifestFile = "spectacular-config.yml";
        def catalogueName = "testCatalogue1"
        return new CatalogueId(catalogueRepo, catalogueManifestFile, catalogueName)
    }

    def "GetInterfaceFileContents returns file contents"() {
        given: "a catalogue with an interface entry"
        def catalogueId = aCatalogue()
        def interfaceEntry = new Interface()

        and: "a spec file location for the interface entry for a .yaml file"
        def specFileRepoId = RepositoryId.createForNameWithOwner("test-owner/test-repo")
        def specFilePath = "spec-file.yaml"
        def specFileLocation = new SpecFileLocation()
                .withRepo(specFileRepoId.nameWithOwner)
                .withFilePath(specFilePath)
        interfaceEntry.setSpecFile(specFileLocation)

        and: "content for the spec file at a given git ref"
        def ref = "some-ref"
        def specFileContentItem = new ContentItem(specFilePath, specFilePath, "some-sha", "type?", null, "dGVzdCBmaWxlIGNvbnRlbnQ=", "base64")

        when: "getting the interface file contents for the interface entry and git ref"
        def result = interfaceService.getInterfaceFileContents(catalogueId, interfaceEntry, ref)

        then: "the file contents are retrieved"
        1 * restApiClient.getRepositoryContent(specFileRepoId, specFilePath, "some-ref") >> specFileContentItem

        and: "there is no error"
        !result.hasError()

        and: "the decoded file contents are returned with yaml media type"
        result.getInterfaceFileContents().contents == "test file content"
        result.getInterfaceFileContents().getMediaTypeGuess().toString() == "application/yaml"
    }

    def "GetInterfaceFileContents uses catalogue repo when spec file location doesn't specify a repo"() {
        given: "a catalogue with an interface entry"
        def catalogueId = aCatalogue()
        def interfaceEntry = new Interface()

        and: "a spec file location for the interface for a .yaml file"
        def specFilePath = "spec-file.yaml"
        def specFileLocation = new SpecFileLocation()
                .withFilePath(specFilePath)
        interfaceEntry.setSpecFile(specFileLocation)

        and: "content for the spec file at a given git ref"
        def ref = "some-ref"
        def specFileContentItem = new ContentItem(specFilePath, specFilePath, "some-sha", "type?", null, "dGVzdCBmaWxlIGNvbnRlbnQ=", "base64")

        when: "getting the interface file contents for the interface entry and git ref"
        def result = interfaceService.getInterfaceFileContents(catalogueId, interfaceEntry, ref)

        then: "the file contents are retrieved from the catalogue's repo"
        1 * restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), specFilePath, "some-ref") >> specFileContentItem

        and: "there is no error"
        !result.hasError()

        and: "the decoded file contents are returned"
        result.getInterfaceFileContents().contents == "test file content"
    }

    def "GetInterfaceFileContents returns config error for an interface without a spec file location"() {
        given: "a catalogue with an interface entry with no spec file location"
        def catalogueId = aCatalogue()
        def interfaceEntry = new Interface()

        when: "getting the interface file contents for the interface entry"
        def result = interfaceService.getInterfaceFileContents(catalogueId, interfaceEntry, "some-ref")

        then: "a config error is returned"
        result.hasError()
        result.getError().getType() == GetCatalogueManifestConfigurationItemErrorType.CONFIG_ERROR
    }

    def "GetInterfaceFileContents returns not found error for a spec file location that does not exist"() {
        given: "a catalogue with an interface entry"
        def catalogueId = aCatalogue()
        def interfaceEntry = new Interface()

        and: "a spec file location for the interface for a .yaml file"
        def specFilePath = "spec-file.yaml"
        def specFileLocation = new SpecFileLocation()
                .withFilePath(specFilePath)
        interfaceEntry.setSpecFile(specFileLocation)

        when: "getting the interface file contents for the interface entry and git ref"
        def result = interfaceService.getInterfaceFileContents(catalogueId, interfaceEntry, "some-ref")

        then: "the missing file contents are retrieved"
        1 * restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), specFilePath, "some-ref") >> { throw new HttpClientErrorException(HttpStatus.NOT_FOUND) }

        and: "a not found error is returned"
        result.hasError()
        result.getError().getType() == GetCatalogueManifestConfigurationItemErrorType.NOT_FOUND
    }

    def "GetInterface has SpecEvolution and SpecEvolutionSummary"() {
        given: "a catalogue with an interface entry"
        def catalogueId = aCatalogue()
        def interfaceEntryName = "testInterface"
        def interfaceEntry = new Interface()

        and: "a spec file location for the interface for a .yaml file"
        def specFileRepoId = RepositoryId.createForNameWithOwner("test-owner/test-repo")
        def specFilePath = "spec-file.yaml"
        def specFileLocation = new SpecFileLocation()
                .withRepo(specFileRepoId.nameWithOwner)
                .withFilePath(specFilePath)
        interfaceEntry.setSpecFile(specFileLocation)

        and: "a spec evolution config"
        def specEvolutionConfig = new SpecEvolutionConfig()
        interfaceEntry.setSpecEvolutionConfig(specEvolutionConfig)

        and: "a generate spec evolution"
        def specEvolution = Mock(SpecEvolution)

        and: "a spec evolution summary"
        def specEvolutionSummary = Mock(SpecEvolutionSummary)

        when: "getting the spec evolution for the interface entry"
        def result = interfaceService.getInterfaceDetails(catalogueId, interfaceEntry, interfaceEntryName)

        then: "the spec evolution for the interface is retrieved"
        1 * specEvolutionService.getSpecEvolution(interfaceEntryName, specEvolutionConfig, specFileRepoId, specFilePath) >> specEvolution

        and: "the spec evolution summary is created"
        1 * specEvolutionSummaryMapper.mapSpecEvolutionToSummary(specEvolution) >> specEvolutionSummary

        and: "the interface details is returned with the spec evolution and spec evolution summary"
        result
        result.getSpecEvolution() == specEvolution
        result.getSpecEvolutionSummary() == specEvolutionSummary
    }
}
