package spectacular.backend.cataloguemanifest

import spectacular.backend.cataloguemanifest.model.Catalogue
import spectacular.backend.cataloguemanifest.model.Interface
import spectacular.backend.cataloguemanifest.model.Interfaces
import spectacular.backend.common.CatalogueId
import spectacular.backend.common.CatalogueManifestId
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.domain.ContentItem
import spock.lang.Specification

class CatalogueEntryConfigurationResolverTest extends Specification {
    def catalogueManifestParser = Mock(CatalogueManifestParser)
    def catalogueManifestProvider = Mock(CatalogueManifestProvider)
    def catalogueEntryConfigurationResolver = new CatalogueEntryConfigurationResolver(catalogueManifestParser, catalogueManifestProvider)

    def catalogueManifestYmlFilename = "spectacular-config.yml"
    def aUsername = "test-user"

    def aCatalogueManifestId() {
        def catalogueRepo = new RepositoryId("test-owner","test-repo987")
        def catalogueManifestFile = catalogueManifestYmlFilename;
        return new CatalogueManifestId(catalogueRepo, catalogueManifestFile)
    }

    def aCatalogueId() {
        def catalogueRepo = new RepositoryId("test-owner","test-repo987")
        def catalogueManifestFile = catalogueManifestYmlFilename;
        def catalogueName = "testCatalogue1"
        return new CatalogueId(catalogueRepo, catalogueManifestFile, catalogueName)
    }

    def aCatalogueManifest(String interfaceEntryName, Interface interfaceEntry) {
        def interfaces = new Interfaces().withAdditionalProperty(interfaceEntryName, interfaceEntry)
        return new Catalogue().withInterfaces(interfaces)
    }

    def "GetCatalogueEntryConfiguration returns the catalogue config for a catalogue with no errors"() {
        given: "a location for a catalogue config"
        def catalogueId = aCatalogueId()

        and: "a catalogue manifest file at that location"
        def catalogueManifestFileContents = Mock(ContentItem)
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createSuccessfulResult(catalogueId, catalogueManifestFileContents)

        and: "a catalogue config entry in the manifest file with an interface entry in it"
        def interfaceEntry = Mock(Interface)
        def interfaceEntryName = "testInterface1"
        def catalogue = aCatalogueManifest(interfaceEntryName, interfaceEntry)
        def findAndParseCatalogueResult = FindAndParseCatalogueResult.createCatalogueEntryParsedResult(catalogueManifestFileContents, catalogue)

        when: "the catalogue configuration is retrieved"
        def result = catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername)

        then: "the catalogue manifest contents is retrieved"
        1 * catalogueManifestProvider.getCatalogueManifest(catalogueId, aUsername) >> getCatalogueManifestFileContentResult

        and: "catalogue config is found and parsed from the catalogue manifest contents"
        1 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(catalogueManifestFileContents, catalogueId.getCatalogueName()) >> findAndParseCatalogueResult

        and: "the result has no error"
        !result.hasError()

        and: "the catalogue entry is returned"
        result.getCatalogueEntry() == catalogue
    }

    def "GetCatalogueEntryConfiguration returns not found error for a catalogue manifest file that doesn't exist"() {
        given: "a location for a catalogue config"
        def catalogueId = aCatalogueId()

        and: "no catalogue manifest file at that location"
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createNotFoundResult(catalogueId)

        when: "the catalogue configuration is retrieved"
        def result = catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername)

        then: "the catalogue manifest contents is retrieved"
        1 * catalogueManifestProvider.getCatalogueManifest(catalogueId, aUsername) >> getCatalogueManifestFileContentResult

        and: "the result has a not found error"
        result.hasError()
        result.getError().getType() == GetCatalogueManifestConfigurationItemErrorType.NOT_FOUND
    }

    def "GetCatalogueEntryConfiguration returns not found error for a catalogue manifest that doesn't contain the catalogue entry" () {
        given: "a location for a catalogue config"
        def catalogueId = aCatalogueId()

        and: "a catalogue manifest file at that location"
        def catalogueManifestFileContents = Mock(ContentItem)
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createSuccessfulResult(catalogueId, catalogueManifestFileContents)

        and: "no catalogue config entry in the manifest file"
        def findAndParseCatalogueResult = FindAndParseCatalogueResult.createCatalogueEntryNotFoundResult(catalogueManifestFileContents)

        when: "the catalogue configuration is retrieved"
        def result = catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getCatalogueManifest(catalogueId, aUsername) >> getCatalogueManifestFileContentResult

        and: "catalogue config is attempted to be found and parsed from the catalogue manifest contents"
        1 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(catalogueManifestFileContents, catalogueId.getCatalogueName()) >> findAndParseCatalogueResult

        and: "the result has a not found error"
        result.hasError()
        result.getError().getType() == GetCatalogueManifestConfigurationItemErrorType.NOT_FOUND
    }

    def "GetCatalogueEntryConfiguration returns a catalogue with parse error for a catalogue manifest that doesn't parse"() {
        given: "a location for a catalogue config"
        def catalogueId = aCatalogueId()

        and: "a catalogue manifest file at that location"
        def catalogueManifestFileContents = Mock(ContentItem)
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createSuccessfulResult(catalogueId, catalogueManifestFileContents)

        and: "a catalogue config entry in the manifest file with parse errors"
        def parseErrorMessage = "test error"
        def findAndParseCatalogueResult = FindAndParseCatalogueResult.createCatalogueEntryParseErrorResult(catalogueManifestFileContents, parseErrorMessage)

        when: "the catalogue configuration is retrieved"
        def result = catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getCatalogueManifest(catalogueId, aUsername) >> getCatalogueManifestFileContentResult

        and: "catalogue config is attempted to be found and parsed from the catalogue manifest contents"
        1 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(catalogueManifestFileContents, catalogueId.getCatalogueName()) >> findAndParseCatalogueResult

        and: "the result has a not found error"
        result.hasError()
        result.getError().getType() == GetCatalogueManifestConfigurationItemErrorType.CONFIG_ERROR
    }
}
