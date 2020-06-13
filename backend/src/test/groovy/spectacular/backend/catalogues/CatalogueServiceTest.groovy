package spectacular.backend.catalogues

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spectacular.backend.api.model.Catalogue
import spectacular.backend.api.model.SpecLog
import spectacular.backend.cataloguemanifest.CatalogueManifestParseResult
import spectacular.backend.cataloguemanifest.CatalogueManifestParser
import spectacular.backend.cataloguemanifest.FindAndParseCatalogueResult
import spectacular.backend.cataloguemanifest.model.CatalogueManifest
import spectacular.backend.cataloguemanifest.model.Interface
import spectacular.backend.cataloguemanifest.model.Interfaces
import spectacular.backend.common.CatalogueId
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.domain.ContentItem
import spectacular.backend.github.domain.SearchCodeResultItem
import spectacular.backend.github.domain.SearchCodeResults
import spectacular.backend.specs.SpecLogService
import spock.lang.Specification

class CatalogueServiceTest extends Specification {
    def catalogueManifestYamlFilename = "spectacular-config.yaml"
    def catalogueManifestYmlFilename = "spectacular-config.yml"
    def restApiClient = Mock(RestApiClient)
    def specLogService = Mock(SpecLogService)
    def catalogueManifestParser = Mock(CatalogueManifestParser)
    def catalogueMapper = Mock(CatalogueMapper)
    def catalogueService = new CatalogueService(restApiClient, specLogService, catalogueManifestParser, catalogueMapper)

    def aUsername = "test-user"
    def anOrg = "test-org"

    def aCatalogue() {
        def catalogueRepo = new RepositoryId("test-owner","test-repo987")
        def catalogueManifestFile = catalogueManifestYmlFilename;
        def catalogueName = "testCatalogue1"
        return new CatalogueId(catalogueRepo, catalogueManifestFile, catalogueName)
    }

    def aValidYamlManifestFileContentItem() {
        def validYamlManifest = "spectacular: '0.1'\n" +
                "catalogues:\n" +
                "  testCatalogue1:\n" +
                "    title: \"Test Catalogue 1\"\n" +
                "    description: \"Specifications for all the interfaces across system X.\"\n" +
                "    interfaces:\n" +
                "      interface1:\n" +
                "        specFile:\n" +
                "          filePath: \"specs/example-template.yaml\"\n" +
                "      interface2:\n" +
                "        specFile:\n" +
                "          filePath: \"specs/example-spec.yaml\"\n" +
                "          repo: \"test-owner2/specs-test2\""
        def manifestFileContentItem = Mock(ContentItem)
        manifestFileContentItem.getDecodedContent() >> validYamlManifest
        return manifestFileContentItem
    }

    def searchCodeResultsForInstallation(RepositoryId catalogueRepository, boolean isBothFilesPresent = false) {
        def searchCodeResultItems = [];

        def searchCodeResultRepo = new spectacular.backend.github.domain.Repository(1234, catalogueRepository.getNameWithOwner(), new URI("https://test-url"))
        def searchCodeResultItem = new SearchCodeResultItem(catalogueManifestYmlFilename, catalogueManifestYmlFilename, "test_url", "test_git_url", "test_html_url", searchCodeResultRepo)
        searchCodeResultItems.add(searchCodeResultItem)

        if (isBothFilesPresent) {
            def searchCodeResultRepo2 = new spectacular.backend.github.domain.Repository(1234, catalogueRepository.getNameWithOwner(), new URI("https://test-url"))
            def searchCodeResultItem2 = new SearchCodeResultItem(catalogueManifestYamlFilename, catalogueManifestYamlFilename, "test_url", "test_git_url", "test_html_url", searchCodeResultRepo2)
            searchCodeResultItems.add(searchCodeResultItem2)
        }

        return new SearchCodeResults(searchCodeResultItems.size(), searchCodeResultItems, false)
    }

    def "get catalogue for repository and valid user"() {
        given: "a specific catalogue the user has access to"
        def catalogueId = aCatalogue()
        def userAndInstallationAccessToCatalogueRepository = true

        and: "a catalogue config manifest containing the catalogue"
        def manifestFileContentItem = Mock(ContentItem)

        and: "a catalogue config parse result for catalogue in the manifest file contents with catalogue entry"
        def catalogueParseResult = Mock(FindAndParseCatalogueResult)
        def catalogue = Mock(spectacular.backend.cataloguemanifest.model.Catalogue)
        catalogueParseResult.getCatalogue() >> catalogue

        and: "a catalogue API model representation of the catalogue manifest object without spec log items"
        def catalogueModel = Mock(Catalogue)

        and: "spec logs for each file in the manifest"
        def specLogs = [Mock(SpecLog), Mock(SpecLog)]

        when: "the get catalogue for user is called"
        def result = catalogueService.getCatalogueForUser(catalogueId, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), catalogueId.getPath(), null) >> manifestFileContentItem

        and: "the catalogue entry in the manifest file contents is found and parsed"
        1 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(_, catalogueId.getCatalogueName()) >> catalogueParseResult

        and: "the manifest catalogue entry object is mapped to an API catalogue model"
        1 * catalogueMapper.mapCatalogue(_, catalogueId, _) >> catalogueModel

        and: "spec logs are retrieved for the catalogue"
        1 * specLogService.getSpecLogsFor(_, catalogueId) >> specLogs

        and: "the spec logs are added to the catalogue API model object"
        1* catalogueModel.specLogs(specLogs) >> catalogueModel

        and: "the mapped catalogue API model object is returned"
        result == catalogueModel
    }

    def "get catalogue returns null for a repository the user does not have access to"() {
        given: "a specific catalogue the user does not have access to"
        def catalogueId = aCatalogue()
        def userAndInstallationAccessToCatalogueRepository = false

        when: "the get catalogue for user is called"
        def catalogue = catalogueService.getCatalogueForUser(catalogueId, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)

        and: "no catalogue is returned"
        !catalogue

        and: "no spec items are retrieved"
        0 * specLogService.getSpecLogsFor(_, _)
    }

    def "get catalogue returns null for a repository the app installation does not have access to"() {
        given: "a specific catalogue the installation does not have access to"
        def catalogueId = aCatalogue()

        when: "the get catalogue for user is called"
        def catalogue = catalogueService.getCatalogueForUser(catalogueId, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), aUsername) >> { throw new HttpClientErrorException(HttpStatus.FORBIDDEN) }

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)

        and: "no catalogue is returned"
        !catalogue

        and: "no spec items are retrieved"
        0 * specLogService.getSpecLogsFor(_, _)
    }

    def "get catalogue for manifest file that does not exist"() {
        given: "a specific catalogue the user has access to"
        def catalogueId = aCatalogue()
        def userAndInstallationAccessToCatalogueRepository = true

        when: "the get catalogue for user is called"
        def catalogue = catalogueService.getCatalogueForUser(catalogueId, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file does not exist"
        1 * restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), catalogueId.getPath(), null) >> { throw new HttpClientErrorException(HttpStatus.NOT_FOUND) }

        and: "no catalogue is returned"
        !catalogue

        and: "no spec items are retrieved"
        0 * specLogService.getSpecLogsFor(_, _)
    }

    def "find catalogues for valid user and org"() {
        given: "user and app installation access to 1 repository with a catalogue config manifest file"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResults = searchCodeResultsForInstallation(repo)
        def userAndInstallationAccessToCatalogueRepository = true

        and: "valid catalogue manifest YAML content in the manifest file"
        def manifestFileContentItem = Mock(ContentItem)

        and: "a catalogue manifest parse result the manifest file contents with valid catalogue manifest object"
        def catalogueManifestParseResult = Mock(CatalogueManifestParseResult)
        def catalogueManifest = Mock(CatalogueManifest)
        catalogueManifestParseResult.getCatalogueManifest() >> catalogueManifest

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
        def catalogueManifestParseResult = Mock(CatalogueManifestParseResult)
        def catalogueManifest = Mock(CatalogueManifest)
        catalogueManifestParseResult.getCatalogueManifest() >> catalogueManifest

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
        def searchCodeResultRepo = new spectacular.backend.github.domain.Repository(1234, repo.getNameWithOwner(), new URI("https://test-url"))
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

    def "get interface entry from a catalogue"() {
        given: "a specific catalogue the user has access to"
        def catalogueId = aCatalogue()
        def userAndInstallationAccessToCatalogueRepository = true

        and: "an interface name to get an interface entry for"
        def interfaceName = "some-interface"

        and: "a catalogue config manifest containing the catalogue"
        def manifestFileContentItem = Mock(ContentItem)

        and: "a catalogue config parse result with a catalogue entry in the manifest file contents"
        def catalogueParseResult = Mock(FindAndParseCatalogueResult)
        def catalogue = Mock(spectacular.backend.cataloguemanifest.model.Catalogue)
        catalogueParseResult.getCatalogue() >> catalogue

        and: "the parse result catalogue object has an interface entry for the interface name"
        def interfaces = Mock(Interfaces)
        catalogue.getInterfaces() >> interfaces
        def interfaceEntry = Mock(Interface)
        interfaces.getAdditionalProperties() >> ["some-interface": interfaceEntry]

        when: "the get interface entry for user is called with the name of the interface"
        def result = catalogueService.getInterfaceEntry(catalogueId, interfaceName, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), catalogueId.getPath(), null) >> manifestFileContentItem

        and: "the catalogue entry in the manifest file contents is searched for"
        1 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(_, catalogueId.getCatalogueName()) >> catalogueParseResult

        and: "an interface entry is returned for the specific interface name"
        result
    }

    def "get interface entry from catalogue for name that does not exist in manifest"() {
        given: "a specific catalogue the user has access to"
        def catalogueId = aCatalogue()
        def userAndInstallationAccessToCatalogueRepository = true

        and: "an interface name to get an interface entry for"
        def interfaceName = "some-interface"

        and: "a catalogue config manifest containing the catalogue"
        def manifestFileContentItem = Mock(ContentItem)

        and: "a catalogue config parse result without a catalogue entry in the manifest file contents"
        def catalogueParseResult = Mock(FindAndParseCatalogueResult)
        catalogueParseResult.getCatalogue() >> null

        when: "the get interface entry for user is called"
        def interfaceEntry = catalogueService.getInterfaceEntry(catalogueId, interfaceName, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), catalogueId.getPath(), null) >> manifestFileContentItem

        and: "the catalogue entry in the manifest file contents is searched for"
        1 * catalogueManifestParser.findAndParseCatalogueInManifestFileContents(_, catalogueId.getCatalogueName()) >> catalogueParseResult

        and: "no interface entry is found for the specific interface name"
        !interfaceEntry
    }

    def "get interface entry from a catalogue that does not exist"() {
        given: "a specific catalogue the user has access to"
        def catalogueId = aCatalogue()
        def userAndInstallationAccessToCatalogueRepository = true

        and: "an interface name to get an interface entry for"
        def interfaceName = "some-interface"

        when: "the get interface entry for user is called"
        def interfaceEntry = catalogueService.getInterfaceEntry(catalogueId, interfaceName, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), catalogueId.getPath(), null) >> { throw new HttpClientErrorException(HttpStatus.NOT_FOUND) }

        and: "no interface entry is returned"
        !interfaceEntry
    }

    def "get interface entry from a catalogue that the user does not have access to"() {
        given: "a specific catalogue the user has access to"
        def catalogueId = aCatalogue()
        def userAndInstallationAccessToCatalogueRepository = false

        and: "an interface name to get an interface entry for"
        def interfaceName = "some-interface"

        when: "the get interface entry for user is called"
        def interfaceEntry = catalogueService.getInterfaceEntry(catalogueId, interfaceName, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)

        and: "no interface entry is returned"
        !interfaceEntry
    }

    def "get interface entry from a catalogue has parse errors"() {
        given: "a specific catalogue the user has access to"
        def catalogueId = aCatalogue()
        catalogueId = new CatalogueId(catalogueId.getRepositoryId(), catalogueId.getPath(), "someOtherCatalogue")
        def userAndInstallationAccessToCatalogueRepository = true

        and: "an interface name to get an interface entry for"
        def interfaceName = "interface2"

        and: "an invalid catalogue config manifest"
        def manifestFileContentItem = aValidYamlManifestFileContentItem()

        when: "the get interface entry for user is called"
        def interfaceEntry = catalogueService.getInterfaceEntry(catalogueId, interfaceName, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), catalogueId.getPath(), null) >> manifestFileContentItem

        and: "a runtime exception is thrown"
        thrown(RuntimeException)
    }
}
