package spectacular.backend.files


import spectacular.backend.catalogues.Catalogue
import spectacular.backend.catalogues.CatalogueManifest
import spectacular.backend.catalogues.CatalogueService
import spectacular.backend.catalogues.SpecFileLocation
import spectacular.backend.common.Repository
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.domain.ContentItem
import spock.lang.Specification

class FilesServiceTest extends Specification {
    def catalogueServiceMock = Mock(CatalogueService)
    def restApiMock = Mock(RestApiClient)
    def fileService = new FilesService(catalogueServiceMock, restApiMock)

    def "get file content for a spec path from a different repo to the catalogue returns the raw file contents from github"() {
        given: "a user"
        def username = "test-user"

        and: "a catalogue repo with a manifest containing the requested spec file"
        def catalogueRepo = new Repository("test-owner", "catalogue-repo")
        def isInManifest = true

        and: "a spec file repo and path with content"
        def specFileRepo = new Repository("test-owner2", "spec-repo");
        def specFilePath = "test-specs/example-spec.yaml"
        def specFileContent = "test content"
        def specFileContentItem = Mock(ContentItem)
        specFileContentItem.getDecodedContent() >> specFileContent

        when: "retrieving the spec file contents"
        def result = fileService.getFileContent(catalogueRepo, specFileRepo, specFilePath, null, username)

        then: "the presence of the spec file in the catalogue is checked"
        1 * catalogueServiceMock.isSpecFileInCatalogue(catalogueRepo, username, specFileRepo, specFilePath) >> isInManifest

        and: "the contents is retrieved from the spec file repo and path from the github api"
        1 * restApiMock.getRepositoryContent(specFileRepo, specFilePath, null) >> specFileContentItem
        result == specFileContent
    }

    def "get file content for spec path in the same repo as the catalogue returns the raw file contents from github"() {
        given: "a user"
        def username = "test-user"

        and: "a catalogue repo with a manifest containing the requested spec file"
        def catalogueRepo = new Repository("test-owner", "catalogue-repo")
        def isInManifest = true

        and: "a spec file repo and path with content"
        def specFilePath = "test-specs/example-spec.yaml"
        def specFileContent = "test content"
        def specFileContentItem = Mock(ContentItem)
        specFileContentItem.getDecodedContent() >> specFileContent

        when: "retrieving the spec file contents"
        def result = fileService.getFileContent(catalogueRepo, catalogueRepo, specFilePath, null, username)

        then: "the presence of the spec file in the catalogue is checked"
        1 * catalogueServiceMock.isSpecFileInCatalogue(catalogueRepo, username, catalogueRepo, specFilePath) >> isInManifest

        and: "the contents is retrieved from the spec file repo and path from the github api"
        1 * restApiMock.getRepositoryContent(catalogueRepo, specFilePath, null) >> specFileContentItem
        result == specFileContent
    }

    def "get file content retrieves content from the ref specified"() {
        given: "a user"
        def username = "test-user"

        and: "a catalogue repo with a manifest containing the requested spec file"
        def catalogueRepo = new Repository("test-owner", "catalogue-repo")
        def isInManifest = true

        and: "a spec file repo and path with content"
        def specFilePath = "test-specs/example-spec.yaml"
        def specFileContent = "test content"
        def specFileContentItem = Mock(ContentItem)
        specFileContentItem.getDecodedContent() >> specFileContent

        and: "a ref specified for a test branch"
        def refName = "test-branch"

        when: "retrieving the spec file contents"
        def result = fileService.getFileContent(catalogueRepo, catalogueRepo, specFilePath, refName, username)

        then: "the presence of the spec file in the catalogue is checked"
        1 * catalogueServiceMock.isSpecFileInCatalogue(catalogueRepo, username, catalogueRepo, specFilePath) >> isInManifest

        and: "the contents is retrieved from the github api using the ref specified"
        1 * restApiMock.getRepositoryContent(catalogueRepo, specFilePath, "test-branch") >> specFileContentItem
        result == specFileContent
    }

    def "get file content for spec path not contained in catalogue manifest returns null contents"() {
        given: "a user"
        def username = "test-user"

        and: "a catalogue repo with a manifest not containing the requested spec file"
        def catalogueRepo = new Repository("test-owner", "catalogue-repo")
        def isInManifest = false

        and: "a spec file repo and path with content"
        def specFileRepo = new Repository("test-owner2", "spec-repo");
        def specFilePath = "test-specs/example-spec.yaml"
        def specFileContent = "test content"
        def specFileContentItem = Mock(ContentItem)
        specFileContentItem.getDecodedContent() >> specFileContent

        when: "retrieving the spec file contents"
        def result = fileService.getFileContent(catalogueRepo, specFileRepo, specFilePath, null, username)

        then: "the presence of the spec file in the catalogue is checked"
        1 * catalogueServiceMock.isSpecFileInCatalogue(catalogueRepo, username, specFileRepo, specFilePath) >> isInManifest

        and: "no contents is retrieved for the spec file repo and path from the github api"
        0 * restApiMock.getRepositoryContent(specFileRepo, specFilePath, null) >> specFileContentItem
        !result
    }

    def "get file content for catalogue that the user has no access to or does not exist returns null contents"() {
        given: "a user"
        def username = "test-user"

        and: "a catalogue repo the user does not have access to"
        def catalogueRepo = new Repository("test-owner", "catalogue-repo")
        def isInManifest = false

        and: "a spec file repo and path with content"
        def specFileRepo = new Repository("test-owner2", "spec-repo");
        def specFilePath = "test-specs/example-spec.yaml"
        def specFileContent = "test content"
        def specFileContentItem = Mock(ContentItem)
        specFileContentItem.getDecodedContent() >> specFileContent

        when: "retrieving the spec file contents"
        def result = fileService.getFileContent(catalogueRepo, specFileRepo, specFilePath, null, username)

        then: "the presence of the spec file in the catalogue is checked"
        1 * catalogueServiceMock.isSpecFileInCatalogue(catalogueRepo, username, specFileRepo, specFilePath) >> isInManifest

        and: "no contents is retrieved for the spec file repo and path from the github api"
        0 * restApiMock.getRepositoryContent(specFileRepo, specFilePath, null) >> specFileContentItem
        !result
    }
}
