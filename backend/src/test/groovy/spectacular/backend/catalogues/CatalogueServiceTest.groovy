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
import spectacular.backend.cataloguemanifest.model.CatalogueManifest
import spectacular.backend.cataloguemanifest.model.Interface
import spectacular.backend.common.CatalogueId
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.domain.ContentItem
import spectacular.backend.github.domain.SearchCodeResultItem
import spectacular.backend.github.domain.SearchCodeResults
import spectacular.backend.interfaces.InterfaceFileContents
import spectacular.backend.interfaces.InterfaceService

import spock.lang.Specification

class CatalogueServiceTest extends Specification {
    def catalogueManifestYamlFilename = "spectacular-config.yaml"
    def catalogueManifestYmlFilename = "spectacular-config.yml"
    def restApiClient = Mock(RestApiClient)
    def catalogueManifestParser = Mock(CatalogueManifestParser)
    def catalogueManifestProvider = Mock(CatalogueManifestProvider)
    def catalogueMapper = Mock(CatalogueMapper)
    def interfaceService = Mock(InterfaceService)
    def catalogueService = new CatalogueService(restApiClient, catalogueManifestParser, catalogueManifestProvider, catalogueMapper, interfaceService)

    def aUsername = "test-user"
    def anOrg = "test-org"
    def default_branch = null

    def aCatalogueId() {
        def catalogueRepo = new RepositoryId("test-owner","test-repo987")
        def catalogueManifestFile = catalogueManifestYmlFilename;
        def catalogueName = "testCatalogue1"
        return new CatalogueId(catalogueRepo, catalogueManifestFile, catalogueName)
    }

    def searchCodeResultsForInstallation(RepositoryId catalogueRepository, boolean isBothFilesPresent = false) {
        def searchCodeResultItems = [];

        def searchCodeResultRepo = new spectacular.backend.github.domain.Repository(1234, catalogueRepository.getNameWithOwner(), new URI("https://test-url"), default_branch)
        def searchCodeResultItem = new SearchCodeResultItem(catalogueManifestYmlFilename, catalogueManifestYmlFilename, "test_url", "test_git_url", "test_html_url", searchCodeResultRepo)
        searchCodeResultItems.add(searchCodeResultItem)

        if (isBothFilesPresent) {
            def searchCodeResultRepo2 = new spectacular.backend.github.domain.Repository(1234, catalogueRepository.getNameWithOwner(), new URI("https://test-url"), default_branch)
            def searchCodeResultItem2 = new SearchCodeResultItem(catalogueManifestYamlFilename, catalogueManifestYamlFilename, "test_url", "test_git_url", "test_html_url", searchCodeResultRepo2)
            searchCodeResultItems.add(searchCodeResultItem2)
        }

        return new SearchCodeResults(searchCodeResultItems.size(), searchCodeResultItems, false)
    }

    def aCatalogueManifest(String interfaceEntryName, Interface interfaceEntry) {
        def interfaces = new spectacular.backend.cataloguemanifest.model.Interfaces().withAdditionalProperty(interfaceEntryName, interfaceEntry)
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

    def "find catalogues for valid user and org"() {
        given: "user and app installation access to 1 repository with a catalogue config manifest file"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResults = searchCodeResultsForInstallation(repo)
        def userAndInstallationAccessToCatalogueRepository = true

        and: "valid catalogue manifest YAML content in the manifest file"
        def manifestFileContentItem = Mock(ContentItem)

        and: "a catalogue manifest parse result the manifest file contents with valid catalogue manifest object"
        def catalogueManifest = Mock(CatalogueManifest)
        def catalogueManifestParseResult = new CatalogueManifestParseResult(catalogueManifest, null)

        and: "mapped catalogue API models for each catalogue entry in the manifest object"
        def mappedCatalogues = [Mock(Catalogue)]

        when: "the find catalogues for user and org is called"
        def result = catalogueService.findCataloguesForOrgAndUser(anOrg, aUsername)

        then: "github is searched for catalogue manifest files"
        1 * restApiClient.findFiles("spectacular-config", ["yaml", "yml"], "/", anOrg, null) >> searchCodeResults

        and: "github is checked if the user is a collaborator of the found repositories"
        1 * restApiClient.isUserRepositoryCollaborator(repo, aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(repo, catalogueManifestYmlFilename, null) >> manifestFileContentItem

        and: "the manifest file contents are parsed"
        1 * catalogueManifestParser.parseManifestFileContents(_) >> catalogueManifestParseResult

        and: "the manifest catalogue entry object is mapped to an API catalogue model"
        1 * catalogueMapper.mapCatalogueManifestEntries(catalogueManifest, _, _) >> mappedCatalogues

        and: "the catalogues returned contain the mapped entries"
        result.size() == 1
        result.first() == mappedCatalogues.first()
    }

    def "find catalogues for valid user uses .yml config file when files with both extensions is found"() {
        given: "user and app installation access to 1 repository with a both catalogue config manifest file extensions"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResults = searchCodeResultsForInstallation(repo, true)
        def userAndInstallationAccessToCatalogueRepository = true

        and: "valid catalogue manifest YAML content in the manifest file"
        def manifestFileContentItem = Mock(ContentItem)

        and: "a catalogue manifest parse result the manifest file contents with valid catalogue manifest object"
        def catalogueManifest = Mock(CatalogueManifest)
        def catalogueManifestParseResult = new CatalogueManifestParseResult(catalogueManifest, null)

        and: "mapped catalogue API models for each catalogue entry in the manifest object"
        def mappedCatalogues = [Mock(Catalogue)]

        when: "the find catalogues for user and org is called"
        def result = catalogueService.findCataloguesForOrgAndUser(anOrg, aUsername)

        then: "github is searched for catalogue manifest files"
        1 * restApiClient.findFiles("spectacular-config", ["yaml", "yml"], "/", anOrg, null) >> searchCodeResults

        and: "github is checked if the user is a collaborator of the found repositories"
        1 * restApiClient.isUserRepositoryCollaborator(repo, aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file is retrieved"
        1 * restApiClient.getRepositoryContent(repo, catalogueManifestYmlFilename, null) >> manifestFileContentItem

        and: "the manifest file contents are parsed"
        1 * catalogueManifestParser.parseManifestFileContents(_) >> catalogueManifestParseResult

        and: "the manifest catalogue entry object is mapped to an API catalogue model"
        1 * catalogueMapper.mapCatalogueManifestEntries(catalogueManifest, _, _) >> mappedCatalogues

        and: "the catalogues returned contain the mapped entries"
        result.size() == 1
        result.first() == mappedCatalogues.first()
    }

    def "find catalogues filters out repos the user does not have access to"() {
        given: "1 repository with a catalogue config manifest file the user does not have access to"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResults = searchCodeResultsForInstallation(repo)
        def userAndInstallationAccessToCatalogueRepository = false

        when: "the find catalogues for user and org is called"
        def result = catalogueService.findCataloguesForOrgAndUser(anOrg, aUsername)

        then: "github is search for instance manifest files"
        1 * restApiClient.findFiles("spectacular-config", ["yaml", "yml"], "/", anOrg, null) >> searchCodeResults

        and: "github is checked if the user is a collaborator of the found repositories"
        1 * restApiClient.isUserRepositoryCollaborator(repo, aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "no catalogues are returned"
        result.isEmpty()

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)
    }

    def "find catalogues filters out repos the github app installation does not have access to"() {
        given: "1 repository with a catalogue config manifest file the app installation does not have access to"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResults = searchCodeResultsForInstallation(repo)

        when: "the find catalogues for user and org is called"
        def result = catalogueService.findCataloguesForOrgAndUser(anOrg, aUsername)

        then: "github is search for instance manifest files"
        1 * restApiClient.findFiles("spectacular-config", ["yaml", "yml"], "/", anOrg, null) >> searchCodeResults

        and: "github is checked if the user is a collaborator of the found repositories"
        1 * restApiClient.isUserRepositoryCollaborator(repo, aUsername) >> { throw new HttpClientErrorException(HttpStatus.FORBIDDEN) }

        and: "no catalogues are returned"
        result.isEmpty()

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)
    }

    def "find catalogues filters out incorrect filename matches"() {
        given: "an incorrect filename search result"
        def searchResultFilename = "spectacular-app-config.yaml"

        and: "an app installation with access to 1 repository with a catalogue config manifest file"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResultRepo = new spectacular.backend.github.domain.Repository(1234, repo.getNameWithOwner(), new URI("https://test-url"), default_branch)
        def searchCodeResultItem = new SearchCodeResultItem(searchResultFilename, searchResultFilename, "test_url", "test_git_url", "test_html_url", searchCodeResultRepo)
        def searchCodeResults = new SearchCodeResults(1, List.of(searchCodeResultItem), false)

        when: "the find catalogues for user and org is called"
        def result = catalogueService.findCataloguesForOrgAndUser(anOrg, aUsername)

        then: "github is search for instance manifest files"
        1 * restApiClient.findFiles("spectacular-config", ["yaml", "yml"], "/", anOrg, null) >> searchCodeResults

        and: "no catalogues are returned"
        result.isEmpty()

        and: "github is not checked if the user is a collaborator of any found repositories"
        0 * restApiClient.isUserRepositoryCollaborator(*_)

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)
    }
}
