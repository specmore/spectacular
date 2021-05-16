package spectacular.backend.cataloguemanifest

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spectacular.backend.api.model.Catalogue
import spectacular.backend.cataloguemanifest.model.CatalogueManifest
import spectacular.backend.common.CatalogueId
import spectacular.backend.common.CatalogueManifestId
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.domain.ContentItem
import spectacular.backend.github.domain.Repository
import spectacular.backend.github.domain.RepositoryTopics
import spectacular.backend.github.domain.SearchCodeResultItem
import spectacular.backend.github.domain.SearchCodeResults
import spock.lang.Specification

class CatalogueManifestProviderTest extends Specification {
    def restApiClient = Mock(RestApiClient)
    def catalogueManifestProvider = new CatalogueManifestProvider(restApiClient)

    def catalogueManifestYamlFilename = "spectacular-config.yaml"
    def catalogueManifestYmlFilename = "spectacular-config.yml"

    def aUsername = "test-user"
    def anOrg = "test-org"

    def aCatalogueManifestId() {
        def catalogueRepo = new RepositoryId("test-owner","test-repo987")
        def catalogueManifestFile = catalogueManifestYmlFilename;
        return new CatalogueManifestId(catalogueRepo, catalogueManifestFile)
    }

    def searchCodeResultsForInstallation(RepositoryId catalogueRepository, boolean isBothFilesPresent = false) {
        def searchCodeResultItems = [];

        def searchCodeResultRepo = new Repository(1234, catalogueRepository.getNameWithOwner(), new URI("https://test-url"), null)
        def searchCodeResultItem = new SearchCodeResultItem(catalogueManifestYmlFilename, catalogueManifestYmlFilename, "test_url", "test_git_url", "test_html_url", searchCodeResultRepo)
        searchCodeResultItems.add(searchCodeResultItem)

        if (isBothFilesPresent) {
            def searchCodeResultRepo2 = new Repository(1234, catalogueRepository.getNameWithOwner(), new URI("https://test-url"), null)
            def searchCodeResultItem2 = new SearchCodeResultItem(catalogueManifestYamlFilename, catalogueManifestYamlFilename, "test_url", "test_git_url", "test_html_url", searchCodeResultRepo2)
            searchCodeResultItems.add(searchCodeResultItem2)
        }

        return new SearchCodeResults(searchCodeResultItems.size(), searchCodeResultItems, false)
    }

    def "getCatalogueManifest returns a manifest file doesn't exist result if the user or the app doesn't have access to the repo containing the manifest file"() {
        given: "a catalogue manifest file at a location the user or the app does not have access to"
        def catalogueManifestId = aCatalogueManifestId()
        def userAndInstallationAccessToCatalogueRepository = false

        when: "the getCatalogueManifest is called by the user"
        def result = catalogueManifestProvider.getCatalogueManifest(catalogueManifestId, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueManifestId.getRepositoryId(), aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)

        and: "no repository topics are retrieved"
        0 * restApiClient.getRepositoryTopics(*_)

        and: "the result is for a manifest file that doesn't exist"
        result.isFileNotFoundResult()
    }

    def "getCatalogueManifest returns a successful result"() {
        given: "a catalogue at a location the user or app does have access to"
        def catalogueManifestId = aCatalogueManifestId()
        def userAndInstallationAccessToCatalogueRepository = true

        and: "there is a manifest file at the location specified"
        def manifestFileContentItem = Mock(ContentItem)

        and: "the repo the manifest file is in has topics"
        def repositoryTopics = Mock(RepositoryTopics)

        when: "the getCatalogueManifest is called by the user"
        def result = catalogueManifestProvider.getCatalogueManifest(catalogueManifestId, aUsername)

        then: "github is checked if the user is a collaborator of the repository"
        1 * restApiClient.isUserRepositoryCollaborator(catalogueManifestId.getRepositoryId(), aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(catalogueManifestId.getRepositoryId(), catalogueManifestId.getPath(), null) >> manifestFileContentItem

        and: "the topics of the repository the manifest file is in are retrieved"
        1 * restApiClient.getRepositoryTopics(catalogueManifestId.getRepositoryId()) >> repositoryTopics

        and: "the result is for a manifest file that does exist"
        !result.isFileNotFoundResult()

        and: "the result has the content item"
        result.getCatalogueManifestContent() == manifestFileContentItem

        and: "the result has the repository topics"
        result.getRepositoryTopics() == repositoryTopics
    }

    def "findCatalogueManifestsForOrg returns all catalogue manifest file locations and their content that the user and app has access to for an org"() {
        given: "user and app installation access to a repository with a catalogue config manifest file"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResults = searchCodeResultsForInstallation(repo)
        def userAndInstallationAccessToCatalogueRepository = true

        and: "valid catalogue manifest YAML content in the manifest file"
        def manifestFileContentItem = Mock(ContentItem)

        and: "the repo the manifest file is in has topics"
        def repositoryTopics = Mock(RepositoryTopics)

        when: "the find catalogues for user and org is called"
        def result = catalogueManifestProvider.findCatalogueManifestsForOrg(anOrg, aUsername)

        then: "github is searched for catalogue manifest files"
        1 * restApiClient.findFiles("spectacular-config", ["yaml", "yml"], "/", anOrg, null) >> searchCodeResults

        and: "github is checked if the user is a collaborator of the found repository"
        1 * restApiClient.isUserRepositoryCollaborator(repo, aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(repo, catalogueManifestYmlFilename, null) >> manifestFileContentItem

        and: "the topics of the repository the manifest file is in are retrieved"
        1 * restApiClient.getRepositoryTopics(repo) >> repositoryTopics

        and: "the find catalogues result returns the location and content of the found manifest file"
        result.size() == 1
        result.first().getCatalogueManifestContent() == manifestFileContentItem
        result.first().getCatalogueManifestId().getRepositoryId() == repo
        result.first().getRepositoryTopics() == repositoryTopics
    }

    def "findCatalogueManifestsForOrg returns only one manifest file when both file extensions are present in a repository"() {
        given: "user and app installation access to a repository with both catalogue config manifest file extensions present"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResults = searchCodeResultsForInstallation(repo, true)
        def userAndInstallationAccessToCatalogueRepository = true

        and: "valid catalogue manifest YAML content in the manifest file"
        def manifestFileContentItem = Mock(ContentItem)

        and: "the repo the manifest file is in has topics"
        def repositoryTopics = Mock(RepositoryTopics)

        when: "the find catalogues for user and org is called"
        def result = catalogueManifestProvider.findCatalogueManifestsForOrg(anOrg, aUsername)

        then: "github is searched for catalogue manifest files"
        1 * restApiClient.findFiles("spectacular-config", ["yaml", "yml"], "/", anOrg, null) >> searchCodeResults

        and: "github is checked if the user is a collaborator of the found repository"
        1 * restApiClient.isUserRepositoryCollaborator(repo, aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "the .yml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(repo, catalogueManifestYmlFilename, null) >> manifestFileContentItem

        and: "the topics of the repository the manifest file is in are retrieved"
        1 * restApiClient.getRepositoryTopics(repo) >> repositoryTopics

        and: "the find catalogues result returns the location and content of the .yml manifest file"
        result.size() == 1
        result.first().getCatalogueManifestContent() == manifestFileContentItem
        result.first().getCatalogueManifestId().getRepositoryId() == repo
        result.first().getCatalogueManifestId().getPath().endsWith(".yml")
        result.first().getRepositoryTopics() == repositoryTopics
    }

    def "findCatalogueManifestsForOrg filters out repos the user or app does not have access to"() {
        given: "1 repository with a catalogue config manifest file the user or app does not have access to"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResults = searchCodeResultsForInstallation(repo)
        def userAndInstallationAccessToCatalogueRepository = false

        when: "the find catalogues for user and org is called"
        def result = catalogueManifestProvider.findCatalogueManifestsForOrg(anOrg, aUsername)

        then: "github is search for instance manifest files"
        1 * restApiClient.findFiles("spectacular-config", ["yaml", "yml"], "/", anOrg, null) >> searchCodeResults

        and: "github is checked if the user is a collaborator of the found repositories"
        1 * restApiClient.isUserRepositoryCollaborator(repo, aUsername) >> userAndInstallationAccessToCatalogueRepository

        and: "no catalogues are returned"
        result.isEmpty()

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)
    }

    def "findCatalogueManifestsForOrg filters out incorrect filename matches"() {
        given: "an incorrect filename search result"
        def searchResultFilename = "spectacular-app-config.yaml"

        and: "an app installation with access to 1 repository with a catalogue config manifest file"
        def repo = new RepositoryId("test-owner","test-repo987")
        def searchCodeResultRepo = new Repository(1234, repo.getNameWithOwner(), new URI("https://test-url"), null)
        def searchCodeResultItem = new SearchCodeResultItem(searchResultFilename, searchResultFilename, "test_url", "test_git_url", "test_html_url", searchCodeResultRepo)
        def searchCodeResults = new SearchCodeResults(1, List.of(searchCodeResultItem), false)

        when: "the find catalogues for user and org is called"
        def result = catalogueManifestProvider.findCatalogueManifestsForOrg(anOrg, aUsername)

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
