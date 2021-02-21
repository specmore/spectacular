package spectacular.backend.interfaces

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spectacular.backend.cataloguemanifest.model.Interface
import spectacular.backend.cataloguemanifest.model.SpecFileLocation
import spectacular.backend.catalogues.CatalogueService
import spectacular.backend.common.CatalogueId
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.domain.ContentItem
import spectacular.backend.github.domain.Tag
import spock.lang.Specification

class InterfaceServiceTest extends Specification {
    def catalogueService = Mock(CatalogueService)
    def restApiClient = Mock(RestApiClient)
    def interfaceService = new InterfaceService(catalogueService, restApiClient)

    def aUsername = "test-user"

    def aCatalogue() {
        def catalogueRepo = new RepositoryId("test-owner","test-repo987")
        def catalogueManifestFile = "spectacular-config.yml";
        def catalogueName = "testCatalogue1"
        return new CatalogueId(catalogueRepo, catalogueManifestFile, catalogueName)
    }

    def "GetInterfaceFileContents returns file contents"() {
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

        and: "content for the spec file at a given git ref"
        def ref = "some-ref"
        def specFileContentItem = new ContentItem(specFilePath, specFilePath, "some-sha", "type?", null, "dGVzdCBmaWxlIGNvbnRlbnQ=", "base64")

        when: "getting the interface file contents for the interface entry name and git ref"
        def result = interfaceService.getInterfaceFileContents(catalogueId, interfaceEntryName, ref, aUsername)

        then: "the interface entry is retrieved from the catalogue service"
        1 * catalogueService.getInterfaceEntry(catalogueId, interfaceEntryName, aUsername) >> interfaceEntry

        and: "the file contents are retrieved"
        1 * restApiClient.getRepositoryContent(specFileRepoId, specFilePath, "some-ref") >> specFileContentItem

        and: "the decoded file contents are returned with yaml media type"
        result.contents == "test file content"
        result.getMediaTypeGuess().toString() == "application/yaml"
    }

    def "GetInterfaceFileContents uses catalogue repo when spec file location doesn't specify a repo"() {
        given: "a catalogue with an interface entry"
        def catalogueId = aCatalogue()
        def interfaceEntryName = "testInterface"
        def interfaceEntry = new Interface()

        and: "a spec file location for the interface for a .yaml file"
        def specFilePath = "spec-file.yaml"
        def specFileLocation = new SpecFileLocation()
                .withFilePath(specFilePath)
        interfaceEntry.setSpecFile(specFileLocation)

        and: "content for the spec file at a given git ref"
        def ref = "some-ref"
        def specFileContentItem = new ContentItem(specFilePath, specFilePath, "some-sha", "type?", null, "dGVzdCBmaWxlIGNvbnRlbnQ=", "base64")

        when: "getting the interface file contents for the interface entry name and git ref"
        def result = interfaceService.getInterfaceFileContents(catalogueId, interfaceEntryName, ref, aUsername)

        then: "the interface entry is retrieved from the catalogue service"
        1 * catalogueService.getInterfaceEntry(catalogueId, interfaceEntryName, aUsername) >> interfaceEntry

        and: "the file contents are retrieved"
        1 * restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), specFilePath, "some-ref") >> specFileContentItem

        and: "the decoded file contents are returned"
        result.contents == "test file content"
    }

    def "GetInterfaceFileContents returns null for interfaceName not found in catalogue"() {
        given: "a catalogue"
        def catalogueId = aCatalogue()

        and: "an interface entry name that does not exist in the catalogue"
        def interfaceEntryName = "missingInterface"

        when: "getting the interface file contents for the interface entry name"
        def result = interfaceService.getInterfaceFileContents(catalogueId, interfaceEntryName, "some-ref", aUsername)

        then: "the interface entry is retrieved from the catalogue service"
        1 * catalogueService.getInterfaceEntry(catalogueId, interfaceEntryName, aUsername) >> null

        and: "a null interface file contents is returned"
        !result
    }

    def "GetInterfaceFileContents returns null for an interface without a spec file location"() {
        given: "a catalogue with an interface entry with no spec file location"
        def catalogueId = aCatalogue()
        def interfaceEntryName = "testInterface"
        def interfaceEntry = new Interface()

        when: "getting the interface file contents for the interface entry name"
        def result = interfaceService.getInterfaceFileContents(catalogueId, interfaceEntryName, "some-ref", aUsername)

        then: "the interface entry is retrieved from the catalogue service"
        1 * catalogueService.getInterfaceEntry(catalogueId, interfaceEntryName, aUsername) >> null

        and: "a null interface file contents is returned"
        !result
    }

    def "GetInterfaceFileContents returns null for a spec file location that does not exist"() {

        given: "a catalogue with an interface entry"
        def catalogueId = aCatalogue()
        def interfaceEntryName = "testInterface"
        def interfaceEntry = new Interface()

        and: "a spec file location for the interface for a .yaml file"
        def specFilePath = "spec-file.yaml"
        def specFileLocation = new SpecFileLocation()
                .withFilePath(specFilePath)
        interfaceEntry.setSpecFile(specFileLocation)

        when: "getting the interface file contents for the interface entry name and git ref"
        def result = interfaceService.getInterfaceFileContents(catalogueId, interfaceEntryName, "some-ref", aUsername)

        then: "the interface entry is retrieved from the catalogue service"
        1 * catalogueService.getInterfaceEntry(catalogueId, interfaceEntryName, aUsername) >> interfaceEntry

        and: "the missing file contents are retrieved"
        1 * restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), specFilePath, "some-ref") >> { throw new HttpClientErrorException(HttpStatus.NOT_FOUND) }

        and: "a null interface file contents is returned"
        !result
    }

    def "GetSpecEvolution returns a Spec Evolution based on the tags and branches"() {
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

        and: "the repository has tags"
        def repositoryTags = [ new Tag("testTag1") ]

        when: "getting the spec evolution for the interface entry name"
        def specEvolution = interfaceService.getSpecEvolution(catalogueId, interfaceEntryName, aUsername)

        then: "the interface entry is retrieved from the catalogue service"
        1 * catalogueService.getInterfaceEntry(catalogueId, interfaceEntryName, aUsername) >> interfaceEntry

        and: "the tags are retrieved from the repository the spec file is in"
        1 * restApiClient.getRepositoryTags(specFileRepoId) >> repositoryTags

        and: "the spec evolution's main branch has an item for the tag"
        specEvolution.getMain().getEvolutionItems().size() == 1
    }
}
