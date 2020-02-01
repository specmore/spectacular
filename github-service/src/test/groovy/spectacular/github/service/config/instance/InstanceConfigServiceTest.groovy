package spectacular.github.service.config.instance


import spectacular.github.service.github.RestApiClient
import spectacular.github.service.github.app.AppInstallationContextProvider
import spectacular.github.service.common.Repository
import spectacular.github.service.github.domain.SearchCodeResultItem
import spectacular.github.service.github.domain.SearchCodeResults
import spock.lang.Specification

class InstanceConfigServiceTest extends Specification {
    def instanceManifestFilename = "spectacular-app-config.yaml"
    def restApiClient = Mock(RestApiClient)
    def appInstallationContextProvider = Mock(AppInstallationContextProvider)
    def instanceConfigService = new InstanceConfigService(restApiClient, appInstallationContextProvider)


    def "get instance config for valid repository and installation"() {
        given: "a valid github repository and installation id"
        def repo = new Repository("test-owner", "test-repo", null)
        appInstallationContextProvider.getInstallationId() >> "99"

        and: "a valid Yaml instance config Manifest"
        def validYamlManifest = "catalogues:\n" +
                "- name: \"Test Catalogue 1\"\n" +
                "  repo: test-owner/test-config-repo"

        when: "the instance config service is called"
        def instanceConfig = instanceConfigService.getInstanceConfigForRepository(repo)

        then: "a valid instance config is returned"
        instanceConfig
        instanceConfig.getRepository() == repo
        instanceConfig.getInstallationId() == "99"

        and: "the yaml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(repo, "spectacular-app-config.yaml", null) >> validYamlManifest

        and: "the instance config contains the values of the manifest"
        instanceConfig.getInstanceConfigManifest()
        !instanceConfig.getInstanceConfigManifest().getCatalogues().isEmpty()
        def catalogue = instanceConfig.getInstanceConfigManifest().getCatalogues()[0]
        catalogue.getName() == "Test Catalogue 1"
        catalogue.getRepo() == "test-owner/test-config-repo"
    }

    def "get instance configs for valid user"() {
        given: "a github user"
        def username = "test-user"
        and: "an app installation with access to 1 repository with an instance config manifest"
        appInstallationContextProvider.getInstallationId() >> "99"
        def repo = new Repository("test-owner","test-repo987")
        def searchCodeResultRepo = new spectacular.github.service.github.domain.Repository(1234, repo.getNameWithOwner(), null)
        def searchCodeResultItem = new SearchCodeResultItem(instanceManifestFilename, instanceManifestFilename, "test_url", "test_git_url", "test_html_url", searchCodeResultRepo)
        def searchCodeResults = new SearchCodeResults(1, List.of(searchCodeResultItem), false)
        and: "a valid Yaml instance config Manifest"
        def validYamlManifest = "catalogues:\n" +
                "- name: \"Test Catalogue 1\"\n" +
                "  repo: test-owner/test-config-repo"

        when: "the get instance configs for a user"
        def result = instanceConfigService.getInstanceConfigsForUser(username)

        then: "github is search for instance manifest files"
        1 * restApiClient.findFiles("spectacular-app-config", "yaml", "/", null) >> searchCodeResults

        and: "github is checked if the user is a collaborator of each repository returned"
        1 * restApiClient.isUserRepositoryCollaborator(repo, username) >> true

        and: "a list of 1 instance config is returned"
        result.size() == 1
        def instanceConfig = result.get(0)

        and: "the instance config is for the repo with the manifest file"
        instanceConfig.getRepository().getNameWithOwner() == searchCodeResultRepo.getFull_name()

        and: "the yaml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(repo, instanceManifestFilename, null) >> validYamlManifest

        and: "the instance config contains the values of the manifest"
        instanceConfig.getInstanceConfigManifest()
        !instanceConfig.getInstanceConfigManifest().getCatalogues().isEmpty()
        def catalogue = instanceConfig.getInstanceConfigManifest().getCatalogues()[0]
        catalogue.getName() == "Test Catalogue 1"
        catalogue.getRepo() == "test-owner/test-config-repo"
    }

    def "get instance configs for invalid user"() {
        given: "a github user"
        def username = "test-user"
        and: "an app installation with access to 1 repository with an instance config manifest"
        appInstallationContextProvider.getInstallationId() >> "99"
        def repo = new Repository("test-owner","test-repo987")
        def searchCodeResultRepo = new spectacular.github.service.github.domain.Repository(1234, repo.getNameWithOwner(), null)
        def searchCodeResultItem = new SearchCodeResultItem(instanceManifestFilename, instanceManifestFilename, "test_url", "test_git_url", "test_html_url", searchCodeResultRepo)
        def searchCodeResults = new SearchCodeResults(1, List.of(searchCodeResultItem), false)
        and: "a valid Yaml instance config Manifest"
        def validYamlManifest = "catalogues:\n" +
                "- name: \"Test Catalogue 1\"\n" +
                "  repo: test-owner/test-config-repo"

        when: "the get instance configs for a user"
        def result = instanceConfigService.getInstanceConfigsForUser(username)

        then: "github is search for instance manifest files"
        1 * restApiClient.findFiles("spectacular-app-config", "yaml", "/", null) >> searchCodeResults

        and: "github is checked if the user is a collaborator of each repository returned"
        1 * restApiClient.isUserRepositoryCollaborator(repo, username) >> false

        and: "no instance configs are returned"
        result.isEmpty()

        and: "no yaml manifest file contents is retrieved"
        0 * restApiClient.getRepositoryContent(repo, instanceManifestFilename, null)
    }
}
