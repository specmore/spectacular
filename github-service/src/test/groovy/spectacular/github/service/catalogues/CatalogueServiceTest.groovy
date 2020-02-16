package spectacular.github.service.catalogues

import spectacular.github.service.common.Repository
import spectacular.github.service.github.RestApiClient
import spectacular.github.service.github.app.AppInstallationContextProvider
import spectacular.github.service.github.domain.SearchCodeResultItem
import spectacular.github.service.github.domain.SearchCodeResults
import spock.lang.Specification

class CatalogueServiceTest extends Specification {
    def catalogueManifestFilename = "spectacular-config.yaml"
    def restApiClient = Mock(RestApiClient)
    def appInstallationContextProvider = Mock(AppInstallationContextProvider)
    def catalogueService = new CatalogueService(restApiClient, appInstallationContextProvider)

    def "get catalogues for valid user"() {
        given: "a github user"
        def username = "test-user"

        and: "a github org"
        def org = "test-org"

        and: "an app installation with access to 1 repository with a catalogue config manifest file"
        appInstallationContextProvider.getInstallationId() >> "99"
        def repo = new Repository("test-owner","test-repo987")
        def searchCodeResultRepo = new spectacular.github.service.github.domain.Repository(1234, repo.getNameWithOwner(), null)
        def searchCodeResultItem = new SearchCodeResultItem(catalogueManifestFilename, catalogueManifestFilename, "test_url", "test_git_url", "test_html_url", searchCodeResultRepo)
        def searchCodeResults = new SearchCodeResults(1, List.of(searchCodeResultItem), false)
        and: "a valid Yaml catalogue config Manifest"
        def validYamlManifest = "name: \"Test Catalogue 1\"\n" +
                "description: \"Specifications for all the interfaces in the across the system X.\"\n" +
                "spec-files: \n" +
                "- file-path: \"specs/example-template.yaml\"\n" +
                "- repo: \"test-owner2/specs-test2\"\n" +
                "  file-path: \"specs/example-spec.yaml\""

        when: "the get catalogues for a user is called"
        def result = catalogueService.getCataloguesForOrgAndUser(org, username)

        then: "github is searched for catalogue manifest files"
        1 * restApiClient.findFiles("spectacular-config", "yaml", "/", org) >> searchCodeResults

        and: "github is checked if the user is a collaborator of each repository returned"
        1 * restApiClient.isUserRepositoryCollaborator(repo, username) >> true

        and: "a list of 1 catalogue is returned"
        result.size() == 1
        def catalogue1 = result.get(0)

        and: "the catalogue config is for the repo with the manifest file"
        catalogue1.getRepository().getNameWithOwner() == searchCodeResultRepo.getFull_name()

        and: "the yaml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(repo, catalogueManifestFilename, null) >> validYamlManifest

        and: "the catalogue config contains the values of the manifest"
        catalogue1.getCatalogueManifest()
        catalogue1.getCatalogueManifest().getName() == "Test Catalogue 1"
        catalogue1.getCatalogueManifest().getDescription() == "Specifications for all the interfaces in the across the system X."

        and: "the catalogue config contains 2 spec files"
        !catalogue1.getCatalogueManifest().getSpecFileLocations().isEmpty()

        def specFile1 = catalogue1.getCatalogueManifest().getSpecFileLocations()[0]
        !specFile1.getRepo()
        specFile1.getFilePath() == "specs/example-template.yaml"

        def specFile2 = catalogue1.getCatalogueManifest().getSpecFileLocations()[1]
        specFile2.getRepo().getNameWithOwner() == "test-owner2/specs-test2"
        specFile2.getFilePath() == "specs/example-spec.yaml"
    }

    def "get catalogues filters out repos the user does not have access to"() {
        given: "a github user"
        def username = "test-user"

        and: "a github org"
        def org = "test-org"

        and: "an app installation with access to 1 repository with a catalogue config manifest file"
        appInstallationContextProvider.getInstallationId() >> "99"
        def repo = new Repository("test-owner","test-repo987")
        def searchCodeResultRepo = new spectacular.github.service.github.domain.Repository(1234, repo.getNameWithOwner(), null)
        def searchCodeResultItem = new SearchCodeResultItem(catalogueManifestFilename, catalogueManifestFilename, "test_url", "test_git_url", "test_html_url", searchCodeResultRepo)
        def searchCodeResults = new SearchCodeResults(1, List.of(searchCodeResultItem), false)
        and: "a valid Yaml catalogue config Manifest"
        def validYamlManifest = "name: \"Test Catalogue 1\"\n" +
                "description: \"Specifications for all the interfaces in the across the system X.\"\n" +
                "spec-files: \n" +
                "- file-path: \"specs/example-template.yaml\"\n" +
                "- repo: \"test-owner2/specs-test2\"\n" +
                "  file-path: \"specs/example-spec.yaml\""

        when: "the get catalogues for a user is called"
        def result = catalogueService.getCataloguesForOrgAndUser(org, username)

        then: "github is search for instance manifest files"
        1 * restApiClient.findFiles("spectacular-config", "yaml", "/", org) >> searchCodeResults

        and: "github is checked if the user is a collaborator of each repository returned"
        1 * restApiClient.isUserRepositoryCollaborator(repo, username) >> false

        and: "no catalogues are returned"
        result.isEmpty()

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)
    }

    def "get catalogues filters out incorrect filename matches"() {
        given: "a github user"
        def username = "test-user"

        and: "a github org"
        def org = "test-org"

        and: "an incorrect filename search result"
        def searchFilename = "spectacular-app-config.yaml"

        and: "an app installation with access to 1 repository with a catalogue config manifest file"
        appInstallationContextProvider.getInstallationId() >> "99"
        def repo = new Repository("test-owner","test-repo987")
        def searchCodeResultRepo = new spectacular.github.service.github.domain.Repository(1234, repo.getNameWithOwner(), null)
        def searchCodeResultItem = new SearchCodeResultItem(searchFilename, searchFilename, "test_url", "test_git_url", "test_html_url", searchCodeResultRepo)
        def searchCodeResults = new SearchCodeResults(1, List.of(searchCodeResultItem), false)
        and: "a valid Yaml catalogue config Manifest"
        def validYamlManifest = "name: \"Test Catalogue 1\"\n" +
                "description: \"Specifications for all the interfaces in the across the system X.\"\n" +
                "spec-files: \n" +
                "- file-path: \"specs/example-template.yaml\"\n" +
                "- repo: \"test-owner2/specs-test2\"\n" +
                "  file-path: \"specs/example-spec.yaml\""

        when: "the get catalogues for a user is called"
        def result = catalogueService.getCataloguesForOrgAndUser(org, username)

        then: "github is search for instance manifest files"
        1 * restApiClient.findFiles("spectacular-config", "yaml", "/", org) >> searchCodeResults

        and: "no catalogues are returned"
        result.isEmpty()

        and: "github is not checked if the user is a collaborator of any repository returned"
        0 * restApiClient.isUserRepositoryCollaborator(*_)

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)
    }

    def "get catalogue for repository and valid user"() {
        given: "a github user"
        def username = "test-user"

        and: "a github repository with a valid Yaml catalogue config Manifest"
        def repo = new spectacular.github.service.github.domain.Repository(123, "test-owner/test-repo987", "test-url")
        def requestRepo = Repository.createRepositoryFrom(repo)
        def validYamlManifest = "name: \"Test Catalogue 1\"\n" +
                "description: \"Specifications for all the interfaces in the across the system X.\"\n" +
                "spec-files: \n" +
                "- file-path: \"specs/example-template.yaml\"\n" +
                "- repo: \"test-owner2/specs-test2\"\n" +
                "  file-path: \"specs/example-spec.yaml\""

        and: "an app installation with access to the repository"
        appInstallationContextProvider.getInstallationId() >> "99"

        when: "the get catalogue for repository and user is called"
        def catalogue = catalogueService.getCatalogueForRepoAndUser(requestRepo, username)

        then: "github is checked if the user is a collaborator of the repository successfully"
        1 * restApiClient.isUserRepositoryCollaborator(requestRepo, username) >> true

        and: "the repository details are retrieved"
        1 * restApiClient.getRepository(requestRepo) >> repo

        and: "the yaml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(_, catalogueManifestFilename, null) >> validYamlManifest

        and: "a valid catalogue is returned for the requested repo"
        catalogue
        catalogue.getRepository() == requestRepo

        and: "the catalogue contains the values of the manifest"
        catalogue.getCatalogueManifest()
        catalogue.getCatalogueManifest().getName() == "Test Catalogue 1"
        catalogue.getCatalogueManifest().getDescription() == "Specifications for all the interfaces in the across the system X."

        and: "the catalogue contains 2 spec files"
        !catalogue.getCatalogueManifest().getSpecFileLocations().isEmpty()

        def specFile1 = catalogue.getCatalogueManifest().getSpecFileLocations()[0]
        !specFile1.getRepo()
        specFile1.getFilePath() == "specs/example-template.yaml"

        def specFile2 = catalogue.getCatalogueManifest().getSpecFileLocations()[1]
        specFile2.getRepo().getNameWithOwner() == "test-owner2/specs-test2"
        specFile2.getFilePath() == "specs/example-spec.yaml"
    }

    def "get catalogue returns null for a repository the user does not have access to"() {
        given: "a github user"
        def username = "test-user"

        and: "a repository with a valid Yaml catalogue config Manifest"
        def repo = new Repository("test-owner","test-repo987")
        def validYamlManifest = "name: \"Test Catalogue 1\"\n" +
                "description: \"Specifications for all the interfaces in the across the system X.\"\n" +
                "spec-files: \n" +
                "- file-path: \"specs/example-template.yaml\"\n" +
                "- repo: \"test-owner2/specs-test2\"\n" +
                "  file-path: \"specs/example-spec.yaml\""

        and: "an app installation with access to the repository"
        appInstallationContextProvider.getInstallationId() >> "99"

        when: "the get catalogue for a user is called"
        def catalogue = catalogueService.getCatalogueForRepoAndUser(repo, username)

        then: "github is checked if the user is a collaborator of the repository and the user is not"
        1 * restApiClient.isUserRepositoryCollaborator(repo, username) >> false

        and: "no repository details are retrieved"
        0 * restApiClient.getRepository(_)

        and: "no file contents are retrieved"
        0 * restApiClient.getRepositoryContent(*_)

        and: "no catalogue is returned"
        !catalogue
    }
}
