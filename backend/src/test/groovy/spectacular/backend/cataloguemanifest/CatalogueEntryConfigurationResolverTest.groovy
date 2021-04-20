package spectacular.backend.cataloguemanifest

import spectacular.backend.cataloguemanifest.catalogueentry.CatalogueEntryConfigurationResolver
import spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemErrorType
import spectacular.backend.cataloguemanifest.model.Catalogue
import spectacular.backend.cataloguemanifest.model.CatalogueManifest
import spectacular.backend.cataloguemanifest.model.Catalogues
import spectacular.backend.cataloguemanifest.model.Interface
import spectacular.backend.cataloguemanifest.model.Interfaces
import spectacular.backend.cataloguemanifest.parse.CatalogueManifestContentItemParseResult
import spectacular.backend.cataloguemanifest.parse.CatalogueManifestParser
import spectacular.backend.cataloguemanifest.parse.FindAndParseCatalogueResult
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
    def anOrg = "test-org"

    def aCatalogueManifestId() {
        def catalogueRepo = new RepositoryId("test-owner","test-repo987")
        def catalogueManifestFile = catalogueManifestYmlFilename;
        return new CatalogueManifestId(catalogueRepo, catalogueManifestFile)
    }

    def aCatalogueManifest(String catalogueEntryName, Catalogue catalogueEntry) {
        def catalogueEntries = new Catalogues().withAdditionalProperty(catalogueEntryName, catalogueEntry)
        return new CatalogueManifest().withCatalogues(catalogueEntries)
    }

    def aCatalogueId() {
        def catalogueRepo = new RepositoryId("test-owner","test-repo987")
        def catalogueManifestFile = catalogueManifestYmlFilename;
        def catalogueName = "testCatalogue1"
        return new CatalogueId(catalogueRepo, catalogueManifestFile, catalogueName)
    }

    def aCatalogueEntry(String interfaceEntryName, Interface interfaceEntry) {
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
        def catalogue = aCatalogueEntry(interfaceEntryName, interfaceEntry)
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
        result.getError().getType() == ConfigurationItemErrorType.NOT_FOUND
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
        result.getError().getType() == ConfigurationItemErrorType.NOT_FOUND
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
        result.getError().getType() == ConfigurationItemErrorType.CONFIG_ERROR
    }

    def "findCataloguesForOrgAndUser returns resolved catalogue configs for each valid catalogue entry in manifest files it finds"() {
        given: "user and org"
        def username = aUsername
        def org = anOrg

        and: "a catalogue manifest file accessible by the org and user"
        def manifestFileId = aCatalogueManifestId()
        def catalogueManifestFileContents = Mock(ContentItem)
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createSuccessfulResult(manifestFileId, catalogueManifestFileContents)

        and: "the manifest has a catalogue entry"
        def catalogueEntry = Mock(Catalogue)
        def catalogueEntryName = "testCatalogue1"
        def catalogueManifestConfig = aCatalogueManifest(catalogueEntryName, catalogueEntry)
        def catalogueManifestContentItemParseResult = Mock(CatalogueManifestContentItemParseResult)
        catalogueManifestContentItemParseResult.getCatalogueManifest() >> catalogueManifestConfig

        when: "the catalogue entries are searched for"
        def result = catalogueEntryConfigurationResolver.findCataloguesForOrgAndUser(org, username)

        then: "the catalogue manifests are found for the org and user"
        1 * catalogueManifestProvider.findCatalogueManifestsForOrg(org, username) >> [getCatalogueManifestFileContentResult]

        and: "manifest config is parsed from the catalogue manifest contents"
        1 * catalogueManifestParser.parseManifestFileContentItem(catalogueManifestFileContents) >> catalogueManifestContentItemParseResult

        and: "the catalogue entries are returned"
        result.size() == 1
        result.first().getCatalogueEntry() == catalogueEntry
    }

    def "findCataloguesForOrgAndUser ignores manifest files without content"() {
        given: "user and org"
        def username = aUsername
        def org = anOrg

        and: "a catalogue manifest file accessible by the org and user has no content found"
        def manifestFileId = aCatalogueManifestId()
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createNotFoundResult(manifestFileId)

        when: "the catalogue entries are searched for"
        def result = catalogueEntryConfigurationResolver.findCataloguesForOrgAndUser(org, username)

        then: "the catalogue manifests are found for the org and user"
        1 * catalogueManifestProvider.findCatalogueManifestsForOrg(org, username) >> [getCatalogueManifestFileContentResult]

        and: "no manifest config is parsed from the catalogue manifest contents"
        0 * catalogueManifestParser.parseManifestFileContentItem(_)

        and: "no catalogue entries are returned"
        !result
    }

    def "findCataloguesForOrgAndUser returns an error catalogue config for a invalid manifest files"() {
        given: "user and org"
        def username = aUsername
        def org = anOrg

        and: "a catalogue manifest file accessible by the org and user"
        def manifestFileId = aCatalogueManifestId()
        def catalogueManifestFileContents = Mock(ContentItem)
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createSuccessfulResult(manifestFileId, catalogueManifestFileContents)

        and: "the manifest with parse error"
        def parseError = "manifest parse error"
        def catalogueManifestContentItemParseResult = Mock(CatalogueManifestContentItemParseResult)
        catalogueManifestContentItemParseResult.getError() >> parseError

        when: "the catalogue entries are searched for"
        def result = catalogueEntryConfigurationResolver.findCataloguesForOrgAndUser(org, username)

        then: "the catalogue manifests are found for the org and user"
        1 * catalogueManifestProvider.findCatalogueManifestsForOrg(org, username) >> [getCatalogueManifestFileContentResult]

        and: "manifest config is parsed from the catalogue manifest contents"
        1 * catalogueManifestParser.parseManifestFileContentItem(catalogueManifestFileContents) >> catalogueManifestContentItemParseResult

        and: "the error catalogue entry is returned"
        result.size() == 1
        result.first().getError().getMessage() == parseError
    }

    def "findCataloguesForOrgAndUser ignores manifest files without catalogue entries"() {
        given: "user and org"
        def username = aUsername
        def org = anOrg

        and: "a catalogue manifest file accessible by the org and user"
        def manifestFileId = aCatalogueManifestId()
        def catalogueManifestFileContents = Mock(ContentItem)
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createSuccessfulResult(manifestFileId, catalogueManifestFileContents)

        and: "the manifest has no catalogues "
        def catalogueManifestConfig = new CatalogueManifest()
        def catalogueManifestContentItemParseResult = Mock(CatalogueManifestContentItemParseResult)
        catalogueManifestContentItemParseResult.getCatalogueManifest() >> catalogueManifestConfig

        when: "the catalogue entries are searched for"
        def result = catalogueEntryConfigurationResolver.findCataloguesForOrgAndUser(org, username)

        then: "the catalogue manifests are found for the org and user"
        1 * catalogueManifestProvider.findCatalogueManifestsForOrg(org, username) >> [getCatalogueManifestFileContentResult]

        and: "manifest config is parsed from the catalogue manifest contents"
        1 * catalogueManifestParser.parseManifestFileContentItem(catalogueManifestFileContents) >> catalogueManifestContentItemParseResult

        and: "no catalogue entries are returned"
        !result
    }
}
