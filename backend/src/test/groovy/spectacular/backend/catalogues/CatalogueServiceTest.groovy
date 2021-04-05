package spectacular.backend.catalogues


import spectacular.backend.api.model.Catalogue
import spectacular.backend.api.model.GetInterfaceResult
import spectacular.backend.api.model.SpecEvolutionSummary
import spectacular.backend.cataloguemanifest.CatalogueEntryConfigurationResolver
import spectacular.backend.cataloguemanifest.CatalogueInterfaceEntryConfigurationResolver
import spectacular.backend.cataloguemanifest.CatalogueManifestContentItemParseResult
import spectacular.backend.cataloguemanifest.CatalogueManifestParser
import spectacular.backend.cataloguemanifest.CatalogueManifestProvider
import spectacular.backend.cataloguemanifest.FindAndParseCatalogueResult
import spectacular.backend.cataloguemanifest.GetCatalogueEntryConfigurationResult
import spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemError
import spectacular.backend.cataloguemanifest.GetCatalogueManifestFileContentResult
import spectacular.backend.cataloguemanifest.model.CatalogueManifest
import spectacular.backend.cataloguemanifest.model.Interface
import spectacular.backend.cataloguemanifest.model.Interfaces
import spectacular.backend.common.CatalogueId
import spectacular.backend.common.CatalogueManifestId
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.domain.ContentItem
import spectacular.backend.interfaces.InterfaceFileContents
import spectacular.backend.interfaces.InterfaceService

import spock.lang.Specification

class CatalogueServiceTest extends Specification {
    def catalogueManifestYmlFilename = "spectacular-config.yml"
    def catalogueManifestParser = Mock(CatalogueManifestParser)
    def catalogueManifestProvider = Mock(CatalogueManifestProvider)
    def catalogueEntryConfigurationResolver = Mock(CatalogueEntryConfigurationResolver)
    def catalogueInterfaceEntryConfigurationResolver = Mock(CatalogueInterfaceEntryConfigurationResolver)
    def catalogueMapper = Mock(CatalogueMapper)
    def interfaceService = Mock(InterfaceService)
    def catalogueService = new CatalogueService(catalogueManifestParser, catalogueManifestProvider, catalogueEntryConfigurationResolver, catalogueInterfaceEntryConfigurationResolver, catalogueMapper, interfaceService)

    def aUsername = "test-user"
    def anOrg = "test-org"
    def aManifestUri = new URI("test-uri")

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
        return new spectacular.backend.cataloguemanifest.model.Catalogue().withInterfaces(interfaces)
    }

    def "get catalogue for repository and valid user returns evolution summaries for each interface in manifest catalogue entry"() {
        given: "a location for a catalogue config"
        def catalogueId = aCatalogueId()

        and: "a catalogue config entry in the manifest file with an interface entry in it"
        def interfaceEntry = Mock(Interface)
        def interfaceEntryName = "testInterface1"
        def catalogue = aCatalogueManifest(interfaceEntryName, interfaceEntry)
        def getCatalogueEntryConfigurationResult = GetCatalogueEntryConfigurationResult.createSuccessfulResult(catalogue, aManifestUri)

        and: "interface details for the interface entry with a spec evolution summary"
        def interfaceDetails = Mock(GetInterfaceResult)
        def interfaceSpecEvolutionSummary = Mock(SpecEvolutionSummary)
        interfaceDetails.getSpecEvolutionSummary() >> interfaceSpecEvolutionSummary

        and: "a catalogue API model representation of the catalogue manifest object without interface details"
        def catalogueDetails = Mock(Catalogue)

        when: "the get catalogue for user is called"
        def result = catalogueService.getCatalogueForUser(catalogueId, aUsername)

        then: "the catalogue entry configuration is resolved"
        1 * catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername) >> getCatalogueEntryConfigurationResult

        and: "the manifest catalogue entry object is mapped to an API catalogue model"
        1 * catalogueMapper.mapCatalogue(catalogue, catalogueId, _) >> catalogueDetails

        and: "the interface details are retrieved for each interface entry in the catalogue"
        1 * interfaceService.getInterfaceDetails(catalogueId, interfaceEntry, interfaceEntryName) >> interfaceDetails

        and: "the spec evolutions are added to the catalogue API model object"
        1 * catalogueDetails.specEvolutionSummaries([interfaceSpecEvolutionSummary]) >> catalogueDetails

        and: "the mapped catalogue API model object is returned"
        result.getCatalogueDetails() == catalogueDetails
    }

    def "get catalogue returns error result for a catalogue entry resolve error"() {
        given: "a location for a catalogue config"
        def catalogueId = aCatalogueId()

        and: "no catalogue manifest file with an error"
        def configItemError = Mock(GetCatalogueManifestConfigurationItemError)
        def getCatalogueEntryConfigurationResult = GetCatalogueEntryConfigurationResult.createErrorResult(configItemError)

        when: "the get catalogue for user is called"
        def result = catalogueService.getCatalogueForUser(catalogueId, aUsername)

        then: "the catalogue entry configuration is resolved"
        1 * catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername) >> getCatalogueEntryConfigurationResult

        and: "no interface details are retrieved"
        0 * interfaceService.getInterfaceDetails(_, _, _)

        and: "a error result is returned with no catalogue details"
        result.getGetConfigurationItemError()
        !result.getCatalogueDetails()
    }

    def "get interface details for valid repository, interface name and user successfully returns interface details"() {
        given: "a location for a catalogue config and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "a catalogue manifest file at that location"
        def catalogueManifestFileContents = Mock(ContentItem)
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createSuccessfulResult(catalogueId, catalogueManifestFileContents)

        and: "a catalogue config entry in the manifest file with the interface entry in it"
        def interfaceEntry = Mock(Interface)
        def catalogue = aCatalogueManifest(interfaceEntryName, interfaceEntry)
        def findAndParseCatalogueResult = FindAndParseCatalogueResult.createCatalogueEntryParsedResult(catalogueManifestFileContents, catalogue)

        and: "interface details for the interface entry with a spec evolution summary"
        def interfaceDetails = Mock(GetInterfaceResult)
        def interfaceSpecEvolutionSummary = Mock(SpecEvolutionSummary)
        interfaceDetails.getSpecEvolutionSummary() >> interfaceSpecEvolutionSummary

        and: "a catalogue API model representation of the catalogue manifest object without interface details"
        def catalogueDetails = Mock(Catalogue)

        when: "the get interface details for user is called"
        def result = catalogueService.getInterfaceDetails(catalogueId, interfaceEntryName, aUsername)

        then: "the catalogue manifest contents is retrieved"
        1 * catalogueManifestProvider.getCatalogueManifest(catalogueId, aUsername) >> getCatalogueManifestFileContentResult

        and: "catalogue config is found and parsed from the catalogue manifest contents"
        1 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(catalogueManifestFileContents, catalogueId.getCatalogueName()) >> findAndParseCatalogueResult

        and: "the interface details are retrieved for the interface entry in the catalogue"
        1 * interfaceService.getInterfaceDetails(catalogueId, interfaceEntry, interfaceEntryName) >> interfaceDetails

        and: "the manifest catalogue entry object is mapped to an API catalogue model"
        1 * catalogueMapper.mapCatalogue(catalogue, catalogueId, _) >> catalogueDetails

        and: "the catalogue API object is added to the interface details"
        1 * interfaceDetails.catalogue(catalogueDetails) >> interfaceDetails

        and: "the mapped catalogue API model object is returned"
        result.getInterfaceResult == interfaceDetails
    }

    def "get interface details returns not found error for a catalogue manifest that doesn't exist"() {
        given: "a catalogue id and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "no catalogue manifest file at that location"
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createNotFoundResult(catalogueId)

        when: "the get interface details for user is called"
        def result = catalogueService.getInterfaceDetails(catalogueId, interfaceEntryName, aUsername)

        then: "the catalogue manifest contents is retrieved"
        1 * catalogueManifestProvider.getCatalogueManifest(catalogueId, aUsername) >> getCatalogueManifestFileContentResult

        and: "no interface details are retrieved"
        0 * interfaceService.getInterfaceDetails(_, _, _)

        and: "a not found result is returned"
        result.getNotFoundErrorMessage()
        !result.getGetInterfaceResult()
    }

    def "get interface details returns not found error for a catalogue manifest that doesn't contain the catalogue entry" () {
        given: "a catalogue id and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "a catalogue manifest file at that location"
        def catalogueManifestFileContents = Mock(ContentItem)
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createSuccessfulResult(catalogueId, catalogueManifestFileContents)

        and: "no catalogue config entry in the manifest file"
        def findAndParseCatalogueResult = FindAndParseCatalogueResult.createCatalogueEntryNotFoundResult(catalogueManifestFileContents)

        when: "the get interface details for user is called"
        def result = catalogueService.getInterfaceDetails(catalogueId, interfaceEntryName, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getCatalogueManifest(catalogueId, aUsername) >> getCatalogueManifestFileContentResult

        and: "catalogue config is attempted to be found and parsed from the catalogue manifest contents"
        1 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(catalogueManifestFileContents, catalogueId.getCatalogueName()) >> findAndParseCatalogueResult

        and: "no interface details are retrieved"
        0 * interfaceService.getInterfaceDetails(_, _, _)

        and: "a not found result is returned"
        result.getNotFoundErrorMessage()
        !result.getGetInterfaceResult()
    }

    def "get interface details returns not found error for a catalogue manifest that doesn't contain the interface entry" () {
        given: "a catalogue id and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "a catalogue manifest file at that location"
        def catalogueManifestFileContents = Mock(ContentItem)
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createSuccessfulResult(catalogueId, catalogueManifestFileContents)

        and: "a catalogue config entry in the manifest file without the interface entry in it"
        def catalogue = new spectacular.backend.cataloguemanifest.model.Catalogue()
        def findAndParseCatalogueResult = FindAndParseCatalogueResult.createCatalogueEntryParsedResult(catalogueManifestFileContents, catalogue)

        when: "the get interface details for user is called"
        def result = catalogueService.getInterfaceDetails(catalogueId, interfaceEntryName, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getCatalogueManifest(catalogueId, aUsername) >> getCatalogueManifestFileContentResult

        and: "catalogue config is attempted to be found and parsed from the catalogue manifest contents"
        1 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(catalogueManifestFileContents, catalogueId.getCatalogueName()) >> findAndParseCatalogueResult

        and: "no interface details are retrieved"
        0 * interfaceService.getInterfaceDetails(_, _, _)

        and: "a not found result is returned"
        result.getNotFoundErrorMessage()
        !result.getGetInterfaceResult()
    }

    def "get interface details returns a config error for a catalogue manifest that doesn't parse"() {
        given: "a catalogue id and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "a catalogue manifest file at that location"
        def catalogueManifestFileContents = Mock(ContentItem)
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createSuccessfulResult(catalogueId, catalogueManifestFileContents)

        and: "a catalogue config entry in the manifest file with parse errors"
        def parseErrorMessage = "test error"
        def findAndParseCatalogueResult = FindAndParseCatalogueResult.createCatalogueEntryParseErrorResult(catalogueManifestFileContents, parseErrorMessage)

        when: "the get interface details for user is called"
        def result = catalogueService.getInterfaceDetails(catalogueId, interfaceEntryName, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getCatalogueManifest(catalogueId, aUsername) >> getCatalogueManifestFileContentResult

        and: "catalogue config is attempted to be found and parsed from the catalogue manifest contents"
        1 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(catalogueManifestFileContents, catalogueId.getCatalogueName()) >> findAndParseCatalogueResult

        and: "no interface details are retrieved"
        0 * interfaceService.getInterfaceDetails(_, _, _)

        and: "a config parse error result is returned"
        result.getConfigErrorMessage()
        !result.getGetInterfaceResult()
    }

    def "get interface details returns a config error an interface entry that doesn't have a spec file"() {
        given: "a catalogue id and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "a catalogue manifest file at that location"
        def catalogueManifestFileContents = Mock(ContentItem)
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createSuccessfulResult(catalogueId, catalogueManifestFileContents)

        and: "a catalogue config entry in the manifest file with the interface entry in it"
        def interfaceEntry = Mock(Interface)
        def catalogue = aCatalogueManifest(interfaceEntryName, interfaceEntry)
        def findAndParseCatalogueResult = FindAndParseCatalogueResult.createCatalogueEntryParsedResult(catalogueManifestFileContents, catalogue)

        when: "the get interface details for user is called"
        def result = catalogueService.getInterfaceDetails(catalogueId, interfaceEntryName, aUsername)

        then: "the catalogue manifest contents is retrieved"
        1 * catalogueManifestProvider.getCatalogueManifest(catalogueId, aUsername) >> getCatalogueManifestFileContentResult

        and: "catalogue config is found and parsed from the catalogue manifest contents"
        1 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(catalogueManifestFileContents, catalogueId.getCatalogueName()) >> findAndParseCatalogueResult

        and: "the interface details are retrieved for the interface entry in the catalogue"
        1 * interfaceService.getInterfaceDetails(catalogueId, interfaceEntry, interfaceEntryName) >> null

        and: "a config parse error result is returned"
        result.getConfigErrorMessage()
        !result.getGetInterfaceResult()
    }

    def "get interface file contents for valid repository, interface name and user successfully returns file contents"() {
        given: "a catalogue id, interface entry name and ref"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"
        def ref = 'branch1'

        and: "a catalogue manifest file at that location"
        def catalogueManifestFileContents = Mock(ContentItem)
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createSuccessfulResult(catalogueId, catalogueManifestFileContents)

        and: "a catalogue config entry in the manifest file with the interface entry in it"
        def interfaceEntry = Mock(Interface)
        def catalogue = aCatalogueManifest(interfaceEntryName, interfaceEntry)
        def findAndParseCatalogueResult = FindAndParseCatalogueResult.createCatalogueEntryParsedResult(catalogueManifestFileContents, catalogue)

        and: "contents for the interface spec file"
        def interfaceFileContents = Mock(InterfaceFileContents)

        when: "the get interface file contents for ref and user is called"
        def result = catalogueService.getInterfaceFileContents(catalogueId, interfaceEntryName, ref, aUsername)

        then: "the catalogue manifest contents is retrieved"
        1 * catalogueManifestProvider.getCatalogueManifest(catalogueId, aUsername) >> getCatalogueManifestFileContentResult

        and: "catalogue config is found and parsed from the catalogue manifest contents"
        1 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(catalogueManifestFileContents, catalogueId.getCatalogueName()) >> findAndParseCatalogueResult

        and: "the interface file contents are retrieved for the interface entry in the catalogue"
        1 * interfaceService.getInterfaceFileContents(catalogueId, interfaceEntry, ref) >> interfaceFileContents

        and: "the interface file contents is returned"
        result == interfaceFileContents
    }

    def "get interface file contents returns not found error for a catalogue manifest that doesn't exist"() {
        given: "a catalogue id, interface entry name and ref"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"
        def ref = 'branch1'

        and: "no catalogue manifest file at that location"
        def getCatalogueManifestFileContentResult = GetCatalogueManifestFileContentResult.createNotFoundResult(catalogueId)

        when: "the get interface file contents for ref and user is called"
        def result = catalogueService.getInterfaceFileContents(catalogueId, interfaceEntryName, ref, aUsername)

        then: "the catalogue manifest contents is retrieved"
        1 * catalogueManifestProvider.getCatalogueManifest(catalogueId, aUsername) >> getCatalogueManifestFileContentResult

        and: "no interface file contents is retrieved"
        0 * interfaceService.getInterfaceFileContents(_, _, _)

        and: "a not found result is returned"
        !result
    }

    def "find catalogues for valid user and org returns catalogues for each catalogue manifest file found with actual valid contents"() {
        given: "user and org"
        def username = aUsername
        def org = anOrg

        and: "a catalogue manifest file with contents"
        def catalogueManifestId = aCatalogueManifestId()
        def fileContentItem = Mock(ContentItem)
        def getCatalogueManifestFileContentsResult = GetCatalogueManifestFileContentResult.createSuccessfulResult(catalogueManifestId, fileContentItem)

        and: "a catalogue manifest object parsed from the manifest file contents"
        def catalogueManifest = Mock(CatalogueManifest)
        def catalogueManifestParseResult = CatalogueManifestContentItemParseResult.createSuccessfulParseResult(catalogueManifest, fileContentItem)

        and: "mapped catalogue API models for each catalogue entry in the manifest object"
        def mappedCatalogues = [Mock(Catalogue)]

        when: "the find catalogues for user and org is called"
        def result = catalogueService.findCataloguesForOrgAndUser(org, username)

        then: "the catalogue manifest provider is searched"
        1 * catalogueManifestProvider.findCatalogueManifestsForOrg(org, username) >> [getCatalogueManifestFileContentsResult]

        and: "the manifest file contents are parsed"
        1 * catalogueManifestParser.parseManifestFileContentItem(fileContentItem) >> catalogueManifestParseResult

        and: "the manifest catalogue entry object is mapped to an API catalogue model"
        1 * catalogueMapper.mapCatalogueManifestEntries(catalogueManifest, catalogueManifestId, _) >> mappedCatalogues

        and: "the catalogues returned contain the mapped entries"
        result.size() == 1
        result.first() == mappedCatalogues.first()
    }

    def "find catalogues ignores catalogue manifest files where no contents can be not found"() {
        given: "user and org"
        def username = aUsername
        def org = anOrg

        and: "a catalogue manifest file match is found but without contents"
        def catalogueManifestId = aCatalogueManifestId()
        def getCatalogueManifestFileContentsResult = GetCatalogueManifestFileContentResult.createNotFoundResult(catalogueManifestId)

        when: "the find catalogues for user and org is called"
        def result = catalogueService.findCataloguesForOrgAndUser(org, username)

        then: "the catalogue manifest provider is searched"
        1 * catalogueManifestProvider.findCatalogueManifestsForOrg(org, username) >> [getCatalogueManifestFileContentsResult]

        and: "no manifest file contents are parsed"
        0 * catalogueManifestParser.parseManifestFileContentItem(_)

        and: "no manifest catalogue entry object is mapped to an API catalogue model"
        0 * catalogueMapper.mapCatalogueManifestEntries(_, _, _)

        and: "no catalogues are returned"
        !result
    }
}
