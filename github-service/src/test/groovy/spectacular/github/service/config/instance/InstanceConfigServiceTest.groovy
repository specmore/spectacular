package spectacular.github.service.config.instance

import spectacular.github.service.config.instance.InstanceConfigService
import spectacular.github.service.github.RestApiClient
import spectacular.github.service.github.domain.Repository
import spock.lang.Specification

class InstanceConfigServiceTest extends Specification {
    def restApiClient = Mock(RestApiClient)
    def instanceConfigService = new InstanceConfigService(restApiClient)


    def "get instance config for valid repository and installation"() {
        given: "a valid github repository and installation id"
        def repo = new Repository("test-owner", "test-repo")
        def installationId = 99

        and: "a valid Yaml instance config Manifest"
        def validYamlManifest = "catalogues:\n" +
                "- name: \"Test Catalogue 1\"\n" +
                "  repo: test-owner/test-config-repo"

        when: "the instance config service is called"
        def instanceConfig = instanceConfigService.getInstanceConfigForRepositoryAndInstallation(repo, installationId)

        then: "a valid instance config is returned"
        instanceConfig
        instanceConfig.getRepository() == repo
        instanceConfig.getInstallationId() == installationId

        and: "the yaml manifest file contents is retrieved"
        1 * restApiClient.getRepositoryContent(repo, "spectacular-app-config.yaml", null) >> validYamlManifest

        and: "the instance config contains the values of the manifest"
        instanceConfig.getInstanceConfigManifest()
        !instanceConfig.getInstanceConfigManifest().getCatalogues().isEmpty()
        def catalogue = instanceConfig.getInstanceConfigManifest().getCatalogues()[0]
        catalogue.getName() == "Test Catalogue 1"
        catalogue.getRepo() == "test-owner/test-config-repo"
    }
}
