package spectacular.backend.cataloguemanifest

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spectacular.backend.common.CatalogueId
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.domain.ContentItem
import spock.lang.Specification

class CatalogueManifestProviderTest extends Specification {
    def restApiClient = Mock(RestApiClient)
    def catalogueManifestParser = Mock(CatalogueManifestParser)
    def catalogueManifestProvider = new CatalogueManifestProvider(restApiClient, catalogueManifestParser)

    def catalogueManifestYmlFilename = "spectacular-config.yml"

    def aUsername = "test-user"

    def aCatalogue() {
        def catalogueRepo = new RepositoryId("test-owner","test-repo987")
        def catalogueManifestFile = catalogueManifestYmlFilename;
        def catalogueName = "testCatalogue1"
        return new CatalogueId(catalogueRepo, catalogueManifestFile, catalogueName)
    }

    def "GetAndParseCatalogueInManifest returns a manifest file doesn't exist result if the user or the app doesn't have access to the repo containing the manifest file"() {
        given: "a catalogue at a location the user or the app does not have access to"
        def catalogueId = aCatalogue()
        def userAndInstallationAccessToCatalogueRepository = false

        when: "the getAndParseCatalogueInManifest is called by the user"
        def result = catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)

        and: "no catalogue manifest content is attempted to be parsed"
        0 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(_, _)

        and: "the result is for a manifest file that doesn't exist"
        !result.isCatalogueManifestFileExists()
    }

    def "GetAndParseCatalogueInManifest returns a manifest file doesn't exist result if catalogue id specifies a manifest file location that doesn't exist"() {
        given: "a catalogue at a location the user or app does have access to"
        def catalogueId = aCatalogue()
        def userAndInstallationAccessToCatalogueRepository = true

        and: "there is no file at the location specified"
        def manifestFileContentItemException = new HttpClientErrorException(HttpStatus.NOT_FOUND)

        when: "the getAndParseCatalogueInManifest is called by the user"
        def result = catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file contents is attempted to be retrieved"
        1 * restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), catalogueId.getPath(), null) >> { throw manifestFileContentItemException }

        and: "no catalogue manifest content is attempted to be parsed"
        0 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(_, _)

        and: "the result is for a manifest file that doesn't exist"
        !result.isCatalogueManifestFileExists()
    }

    def "GetAndParseCatalogueInManifest returns a result with parse error if manifest file contents fails to be decoded"() {
        given: "a catalogue at a location the user or app does have access to"
        def catalogueId = aCatalogue()
        def userAndInstallationAccessToCatalogueRepository = true

        and: "there is a manifest file at the location specified but it cannot be decoded"
        def manifestFileContentItem = Mock(ContentItem)
        def decodedContentException = new UnsupportedEncodingException()

        when: "the getAndParseCatalogueInManifest is called by the user"
        def result = catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), catalogueId.getPath(), null) >> manifestFileContentItem

        and: "the contents is attempted to be decoded"
        1 * manifestFileContentItem.getDecodedContent() >> { throw decodedContentException }

        and: "no catalogue manifest content is attempted to be parsed"
        0 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(_, _)

        and: "the result is for a manifest file that does exist"
        result.isCatalogueManifestFileExists()

        and: "the result has a catalogueParseResult with an error"
        result.getCatalogueParseResult().getError()
    }

    def "GetAndParseCatalogueInManifest returns a result from the manifest parser if the manifest file contents are decoded successfully"() {
        given: "a catalogue at a location the user or app does have access to"
        def catalogueId = aCatalogue()
        def userAndInstallationAccessToCatalogueRepository = true

        and: "there is a manifest file at the location specified that can be decoded"
        def manifestFileContentItem = Mock(ContentItem)
        def decodedContent = "test content"

        and: "the manifest file contents is parsable"
        def findAndParseCatalogueResult = Mock(FindAndParseCatalogueResult)

        when: "the getAndParseCatalogueInManifest is called by the user"
        def result = catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), catalogueId.getPath(), null) >> manifestFileContentItem

        and: "the contents is decoded"
        1 * manifestFileContentItem.getDecodedContent() >> decodedContent

        and: "the catalogue manifest content is parsed"
        1 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(decodedContent, catalogueId.getCatalogueName()) >> findAndParseCatalogueResult

        and: "the result is for a manifest file that does exist"
        result.isCatalogueManifestFileExists()

        and: "the result has a catalogueParseResult with an error"
        result.getCatalogueParseResult() == findAndParseCatalogueResult
    }
}
