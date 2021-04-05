package spectacular.backend.catalogues

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spectacular.backend.api.model.Catalogue
import spectacular.backend.api.model.GetInterfaceResult
import spectacular.backend.api.model.SpecEvolutionSummary
import spectacular.backend.cataloguemanifest.CatalogueManifestParseResult
import spectacular.backend.cataloguemanifest.CatalogueManifestParser
import spectacular.backend.cataloguemanifest.CatalogueManifestProvider
import spectacular.backend.cataloguemanifest.FindAndParseCatalogueResult
import spectacular.backend.cataloguemanifest.GetAndParseCatalogueResult
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
    def catalogueMapper = Mock(CatalogueMapper)
    def interfaceService = Mock(InterfaceService)
    def catalogueService = new CatalogueService(catalogueManifestParser, catalogueManifestProvider, catalogueMapper, interfaceService)

    def aUsername = "test-user"
    def anOrg = "test-org"

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

    def aSuccessfulGetAndParseCatalogueResult(spectacular.backend.cataloguemanifest.model.Catalogue catalogue) {
        def catalogueParseResult = FindAndParseCatalogueResult.createCatalogueEntryParsedResult(catalogue)
        def contentItem = Mock(ContentItem)
        return GetAndParseCatalogueResult.createFoundAndParsedResult(contentItem, catalogueParseResult)
    }

    def aGetAndParseCatalogueResultWithEntryNotFound() {
        def catalogueParseResult = FindAndParseCatalogueResult.createCatalogueEntryNotFoundResult()
        def contentItem = Mock(ContentItem)
        return GetAndParseCatalogueResult.createFoundAndParsedResult(contentItem, catalogueParseResult)
    }

    def aGetAndParseCatalogueResultWithParseError(errorMessage) {
        def catalogueParseResult = FindAndParseCatalogueResult.createCatalogueEntryParseErrorResult(errorMessage)
        def contentItem = Mock(ContentItem)
        return GetAndParseCatalogueResult.createFoundAndParsedResult(contentItem, catalogueParseResult)
    }

    def "get catalogue for repository and valid user returns evolution summaries for each interface in manifest catalogue entry"() {
        given: "a catalogue config in a manifest file with an interface entry"
        def catalogueId = aCatalogueId()
        def interfaceEntry = Mock(Interface)
        def interfaceEntryName = "testInterface1"
        def catalogue = aCatalogueManifest(interfaceEntryName, interfaceEntry)

        and: "a successful catalogue config get and parse result"
        def getAndParseCatalogueResult = aSuccessfulGetAndParseCatalogueResult(catalogue)

        and: "interface details for the interface entry with a spec evolution summary"
        def interfaceDetails = Mock(GetInterfaceResult)
        def interfaceSpecEvolutionSummary = Mock(SpecEvolutionSummary)
        interfaceDetails.getSpecEvolutionSummary() >> interfaceSpecEvolutionSummary

        and: "a catalogue API model representation of the catalogue manifest object without interface details"
        def catalogueDetails = Mock(Catalogue)

        when: "the get catalogue for user is called"
        def result = catalogueService.getCatalogueForUser(catalogueId, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername) >> getAndParseCatalogueResult

        and: "the manifest catalogue entry object is mapped to an API catalogue model"
        1 * catalogueMapper.mapCatalogue(catalogue, catalogueId, _) >> catalogueDetails

        and: "the interface details are retrieved for each interface entry in the catalogue"
        1 * interfaceService.getInterfaceDetails(catalogueId, interfaceEntry, interfaceEntryName) >> interfaceDetails

        and: "the spec evolutions are added to the catalogue API model object"
        1 * catalogueDetails.specEvolutionSummaries([interfaceSpecEvolutionSummary]) >> catalogueDetails

        and: "the mapped catalogue API model object is returned"
        result.getCatalogueDetails() == catalogueDetails
    }

    def "get catalogue returns not found error for a catalogue manifest that doesn't exist"() {
        given: "a specific catalogue in a manifest file that doesn't exists"
        def catalogueId = aCatalogueId()
        def getAndParseCatalogueResult = GetAndParseCatalogueResult.createFileNotFoundResult()

        when: "the get catalogue for user is called"
        def result = catalogueService.getCatalogueForUser(catalogueId, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername) >> getAndParseCatalogueResult

        and: "no interface details are retrieved"
        0 * interfaceService.getInterfaceDetails(_, _, _)

        and: "a not found result is returned with no catalogue details"
        result.getNotFoundErrorMessage()
        !result.getCatalogueDetails()
    }

    def "get catalogue returns not found error for a catalogue manifest that doesn't contain the catalogue entry" () {
        given: "a specific catalogue in a manifest file"
        def catalogueId = aCatalogueId()

        and: "a catalogue config get and parse result for an catalogue entry that doesn't exist"
        def getAndParseCatalogueResult = aGetAndParseCatalogueResultWithEntryNotFound()

        when: "the get catalogue for user is called"
        def result = catalogueService.getCatalogueForUser(catalogueId, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername) >> getAndParseCatalogueResult

        and: "no interface details are retrieved"
        0 * interfaceService.getInterfaceDetails(_, _, _)

        and: "a not found result is returned with no catalogue details"
        result.getNotFoundErrorMessage()
        !result.getCatalogueDetails()
    }

    def "get catalogue returns a catalogue with parse error for a catalogue manifest that doesn't parse"() {
        given: "a specific catalogue identifier for a manifest"
        def catalogueId = aCatalogueId()

        and: "a catalogue config get and parse result with parse errors"
        def parseErrorMessage = "test error"
        def getAndParseCatalogueResult = aGetAndParseCatalogueResultWithParseError(parseErrorMessage)

        and: "a catalogue API model representation of the catalogue manifest object without interface details"
        def catalogueDetails = Mock(Catalogue)

        when: "the get catalogue for user is called"
        def result = catalogueService.getCatalogueForUser(catalogueId, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername) >> getAndParseCatalogueResult

        and: "an API catalogue model is created for the parse error"
        1 * catalogueMapper.createForParseError(parseErrorMessage, catalogueId) >> catalogueDetails

        and: "no interface details are retrieved"
        0 * interfaceService.getInterfaceDetails(_, _, _)

        and: "the catalogue API model with parse error is returned"
        result.getCatalogueDetails() == catalogueDetails
    }

    def "get interface details for valid repository, interface name and user successfully returns interface details"() {
        given: "a catalogue id and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "a successful catalogue config get and parse result"
        def interfaceEntry = Mock(Interface)
        def catalogue = aCatalogueManifest(interfaceEntryName, interfaceEntry)
        def getAndParseCatalogueResult = aSuccessfulGetAndParseCatalogueResult(catalogue)

        and: "interface details for the interface entry with a spec evolution summary"
        def interfaceDetails = Mock(GetInterfaceResult)
        def interfaceSpecEvolutionSummary = Mock(SpecEvolutionSummary)
        interfaceDetails.getSpecEvolutionSummary() >> interfaceSpecEvolutionSummary

        and: "a catalogue API model representation of the catalogue manifest object without interface details"
        def catalogueDetails = Mock(Catalogue)

        when: "the get interface details for user is called"
        def result = catalogueService.getInterfaceDetails(catalogueId, interfaceEntryName, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername) >> getAndParseCatalogueResult

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

        and: "a catalogue config get and parse result for an manifest file that doesn't exist"
        def getAndParseCatalogueResult = GetAndParseCatalogueResult.createFileNotFoundResult()

        when: "the get interface details for user is called"
        def result = catalogueService.getInterfaceDetails(catalogueId, interfaceEntryName, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername) >> getAndParseCatalogueResult

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

        and: "a catalogue config get and parse result for an catalogue entry that doesn't exist"
        def getAndParseCatalogueResult = aGetAndParseCatalogueResultWithEntryNotFound()

        when: "the get interface details for user is called"
        def result = catalogueService.getInterfaceDetails(catalogueId, interfaceEntryName, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername) >> getAndParseCatalogueResult

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

        and: "a catalogue config get and parse result with parse errors"
        def parseErrorMessage = "test error"
        def getAndParseCatalogueResult = aGetAndParseCatalogueResultWithParseError(parseErrorMessage)

        when: "the get interface details for user is called"
        def result = catalogueService.getInterfaceDetails(catalogueId, interfaceEntryName, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername) >> getAndParseCatalogueResult

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

        and: "a successful catalogue config get and parse result"
        def interfaceEntry = Mock(Interface)
        def catalogue = aCatalogueManifest(interfaceEntryName, interfaceEntry)
        def getAndParseCatalogueResult = aSuccessfulGetAndParseCatalogueResult(catalogue)

        when: "the get interface details for user is called"
        def result = catalogueService.getInterfaceDetails(catalogueId, interfaceEntryName, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername) >> getAndParseCatalogueResult

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

        and: "a successful catalogue config get and parse result"
        def interfaceEntry = Mock(Interface)
        def catalogue = aCatalogueManifest(interfaceEntryName, interfaceEntry)
        def getAndParseCatalogueResult = aSuccessfulGetAndParseCatalogueResult(catalogue)

        and: "contents for the interface spec file"
        def interfaceFileContents = Mock(InterfaceFileContents)

        when: "the get interface file contents for ref and user is called"
        def result = catalogueService.getInterfaceFileContents(catalogueId, interfaceEntryName, ref, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername) >> getAndParseCatalogueResult

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

        and: "a catalogue config get and parse result for an manifest file that doesn't exist"
        def getAndParseCatalogueResult = GetAndParseCatalogueResult.createFileNotFoundResult()

        when: "the get interface file contents for ref and user is called"
        def result = catalogueService.getInterfaceFileContents(catalogueId, interfaceEntryName, ref, aUsername)

        then: "catalogue config is retrieved and parsed from the catalogue manifest"
        1 * catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, aUsername) >> getAndParseCatalogueResult

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
        def fileContents = "test contents"
        def fileContentItem = Mock(ContentItem)
        fileContentItem.getDecodedContent() >> fileContents
        def getCatalogueManifestFileContentsResult = GetCatalogueManifestFileContentResult.createSuccessfulResult(catalogueManifestId, fileContentItem)

        and: "a catalogue manifest object parsed from the manifest file contents"
        def catalogueManifest = Mock(CatalogueManifest)
        def catalogueManifestParseResult = new CatalogueManifestParseResult(catalogueManifest, null)

        and: "mapped catalogue API models for each catalogue entry in the manifest object"
        def mappedCatalogues = [Mock(Catalogue)]

        when: "the find catalogues for user and org is called"
        def result = catalogueService.findCataloguesForOrgAndUser(org, username)

        then: "the catalogue manifest provider is searched"
        1 * catalogueManifestProvider.findCatalogueManifestsForOrg(org, username) >> [getCatalogueManifestFileContentsResult]

        and: "the manifest file contents are parsed"
        1 * catalogueManifestParser.parseManifestFileContents(fileContents) >> catalogueManifestParseResult

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
        0 * catalogueManifestParser.parseManifestFileContents(_)

        and: "no manifest catalogue entry object is mapped to an API catalogue model"
        0 * catalogueMapper.mapCatalogueManifestEntries(_, _, _)

        and: "no catalogues are returned"
        !result
    }
}
