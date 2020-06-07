package spectacular.backend.catalogues

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spectacular.backend.api.model.SpecLog
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
    def catalogueService = new CatalogueService(restApiClient, specLogService)

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
        given: "a github user"
        def username = "test-user"

        and: "a specific catalogue they have access to"
        def catalogueId = aCatalogue()
        def userAndInstallationAccessToCatalogueRepository = true

        and: "a catalogue config manifest containing the catalogue"
        def manifestFileContentItem = aValidYamlManifestFileContentItem()

        and: "spec logs for each file in the manifest"
        def specLog1 = Mock(SpecLog)
        def specLog2 = Mock(SpecLog)

        when: "the get catalogue for user is called"
        def catalogue = catalogueService.getCatalogueForUser(catalogueId, username)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), username) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(catalogueId.getRepositoryId(), catalogueId.getPath(), null) >> manifestFileContentItem

        and: "a valid catalogue is returned for specific catalogue"
        catalogue
        catalogue.getName() == catalogueId.getCatalogueName()
        catalogue.getFullPath() == catalogueId.getFullPath()

        and: "the catalogue contains the values of the manifest"
        catalogue.getTitle() == "Test Catalogue 1"
        catalogue.getDescription() == "Specifications for all the interfaces across system X."

        and: "the catalogue config contains 2 spec files"
        catalogue.getInterfaceCount() == 2

        and: "spec logs are retrieved for the catalogue"
        1 * specLogService.getSpecLogsFor(_, catalogueId) >> [specLog1, specLog2]

        and: "the catalogue result contains all the spec logs"
        catalogue.getSpecLogs().size() == 2
        catalogue.getSpecLogs()[0] == specLog1
        catalogue.getSpecLogs()[1] == specLog2
    }

    def "get catalogue returns null for a repository the user does not have access to"() {
        given: "a github user"
        def username = "test-user"

        and: "a specific catalogue they do not have access to"
        def catalogueId = aCatalogue()
        def userAndInstallationAccessToCatalogueRepository = false

        when: "the get catalogue for user is called"
        def catalogue = catalogueService.getCatalogueForUser(catalogueId, username)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), username) >> userAndInstallationAccessToCatalogueRepository

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)

        and: "no catalogue is returned"
        !catalogue

        and: "no spec items are retrieved"
        0 * specLogService.getSpecLogsFor(_, _)
    }

    def "get catalogue returns null for a repository the app installation does not have access to"() {
        given: "a github user"
        def username = "test-user"

        and: "a specific catalogue the installation does not have access to"
        def catalogueId = aCatalogue()

        when: "the get catalogue for user is called"
        def catalogue = catalogueService.getCatalogueForUser(catalogueId, username)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueId.getRepositoryId(), username) >> { throw new HttpClientErrorException(HttpStatus.FORBIDDEN) }

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)

        and: "no catalogue is returned"
        !catalogue

        and: "no spec items are retrieved"
        0 * specLogService.getSpecLogsFor(_, _)
    }

    def "find catalogues for valid user and org"() {
        given: "a github user"
        def username = "test-user"

        and: "a github org"
        def org = "test-org"

        and: "user and app installation access to 1 repository with a catalogue config manifest file"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResults = searchCodeResultsForInstallation(repo)
        def userAndInstallationAccessToCatalogueRepository = true

        and: "valid catalogue manifest YAML content in the manifest file"
        def manifestFileContentItem = aValidYamlManifestFileContentItem()

        when: "the find catalogues for user and org is called"
        def result = catalogueService.findCataloguesForOrgAndUser(org, username)

        then: "github is searched for catalogue manifest files"
        1 * restApiClient.findFiles("spectacular-config", ["yaml", "yml"], "/", org, null) >> searchCodeResults

        and: "github is checked if the user is a collaborator of the found repositories"
        1 * restApiClient.isUserRepositoryCollaborator(repo, username) >> userAndInstallationAccessToCatalogueRepository

        and: "a list of 1 catalogue is returned"
        result.size() == 1
        def catalogue1 = result.get(0)

        and: "the catalogue id is correct"
        catalogue1.getEncodedId()
        CatalogueId.createFrom(new String(catalogue1.getEncodedId())) == new CatalogueId(repo, catalogueManifestYmlFilename, "testCatalogue1")

        and: "the catalogue is from the repo and the manifest file"
        catalogue1.getFullPath() == repo.getNameWithOwner() + "/" + catalogueManifestYmlFilename

        and: "the .yml manifest file is retrieved"
        1 * restApiClient.getRepositoryContent(repo, catalogueManifestYmlFilename, null) >> manifestFileContentItem

        and: "the catalogue config contains the values of the manifest"
        catalogue1.getName() == "testCatalogue1"
        catalogue1.getTitle() == "Test Catalogue 1"
        catalogue1.getDescription() == "Specifications for all the interfaces across system X."

        and: "the catalogue config contains 2 spec files"
        catalogue1.getInterfaceCount() == 2
    }

    def "find catalogues for valid user uses .yml config file when files with both extensions is found"() {
        given: "a github user"
        def username = "test-user"

        and: "a github org"
        def org = "test-org"

        and: "user and app installation access to 1 repository with a both catalogue config manifest file extensions"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResults = searchCodeResultsForInstallation(repo, true)
        def userAndInstallationAccessToCatalogueRepository = true

        and: "valid catalogue manifest YAML content in the manifest file"
        def manifestFileContentItem = aValidYamlManifestFileContentItem()

        when: "the find catalogues for user and org is called"
        def result = catalogueService.findCataloguesForOrgAndUser(org, username)

        then: "github is searched for catalogue manifest files"
        1 * restApiClient.findFiles("spectacular-config", ["yaml", "yml"], "/", org, null) >> searchCodeResults

        and: "github is checked if the user is a collaborator of the found repositories"
        1 * restApiClient.isUserRepositoryCollaborator(repo, username) >> userAndInstallationAccessToCatalogueRepository

        and: "a list of 1 catalogue is returned"
        result.size() == 1
        def catalogue1 = result.get(0)

        and: "the catalogue is from the repo and the .yml manifest file"
        catalogue1.getFullPath() == repo.getNameWithOwner() + "/" + catalogueManifestYmlFilename

        and: "the .yml manifest file is retrieved"
        1 * restApiClient.getRepositoryContent(repo, catalogueManifestYmlFilename, null) >> manifestFileContentItem
    }

    def "find catalogues filters out repos the user does not have access to"() {
        given: "a github user"
        def username = "test-user"

        and: "a github org"
        def org = "test-org"

        and: "1 repository with a catalogue config manifest file the user does not have access to"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResults = searchCodeResultsForInstallation(repo)
        def userAndInstallationAccessToCatalogueRepository = false

        when: "the find catalogues for user and org is called"
        def result = catalogueService.findCataloguesForOrgAndUser(org, username)

        then: "github is search for instance manifest files"
        1 * restApiClient.findFiles("spectacular-config", ["yaml", "yml"], "/", org, null) >> searchCodeResults

        and: "github is checked if the user is a collaborator of the found repositories"
        1 * restApiClient.isUserRepositoryCollaborator(repo, username) >> userAndInstallationAccessToCatalogueRepository

        and: "no catalogues are returned"
        result.isEmpty()

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)
    }

    def "find catalogues filters out repos the github app installation does not have access to"() {
        given: "a github user"
        def username = "test-user"

        and: "a github org"
        def org = "test-org"

        and: "1 repository with a catalogue config manifest file the app installation does not have access to"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResults = searchCodeResultsForInstallation(repo)

        when: "the find catalogues for user and org is called"
        def result = catalogueService.findCataloguesForOrgAndUser(org, username)

        then: "github is search for instance manifest files"
        1 * restApiClient.findFiles("spectacular-config", ["yaml", "yml"], "/", org, null) >> searchCodeResults

        and: "github is checked if the user is a collaborator of the found repositories"
        1 * restApiClient.isUserRepositoryCollaborator(repo, username) >> { throw new HttpClientErrorException(HttpStatus.FORBIDDEN) }

        and: "no catalogues are returned"
        result.isEmpty()

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)
    }

    def "find catalogues filters out incorrect filename matches"() {
        given: "a github user"
        def username = "test-user"

        and: "a github org"
        def org = "test-org"

        and: "an incorrect filename search result"
        def searchResultFilename = "spectacular-app-config.yaml"

        and: "an app installation with access to 1 repository with a catalogue config manifest file"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResultRepo = new spectacular.backend.github.domain.Repository(1234, repo.getNameWithOwner(), new URI("https://test-url"))
        def searchCodeResultItem = new SearchCodeResultItem(searchResultFilename, searchResultFilename, "test_url", "test_git_url", "test_html_url", searchCodeResultRepo)
        def searchCodeResults = new SearchCodeResults(1, List.of(searchCodeResultItem), false)

        when: "the find catalogues for user and org is called"
        def result = catalogueService.findCataloguesForOrgAndUser(org, username)

        then: "github is search for instance manifest files"
        1 * restApiClient.findFiles("spectacular-config", ["yaml", "yml"], "/", org, null) >> searchCodeResults

        and: "no catalogues are returned"
        result.isEmpty()

        and: "github is not checked if the user is a collaborator of any found repositories"
        0 * restApiClient.isUserRepositoryCollaborator(*_)

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)
    }
}
