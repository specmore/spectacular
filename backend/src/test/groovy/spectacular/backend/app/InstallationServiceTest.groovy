package spectacular.backend.app

import org.springframework.util.StringUtils
import spectacular.backend.api.model.Catalogue
import spectacular.backend.api.model.GetInterfaceResult
import spectacular.backend.api.model.SpecEvolutionSummary
import spectacular.backend.cataloguemanifest.model.Interface
import spectacular.backend.common.CatalogueId
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.app.AppApiClient
import spectacular.backend.github.domain.Account
import spectacular.backend.github.domain.Installation
import spock.lang.Specification

class InstallationServiceTest extends Specification {
    def appApiClient = Mock(AppApiClient)
    def installationMapper = Mock(InstallationMapper)
    def installationService = new InstallationService(appApiClient, installationMapper)

    def aCatalogueId() {
        def catalogueRepo = new RepositoryId("test-owner","test-repo987")
        def catalogueManifestFile = "spectacular-config.yml";
        def catalogueName = "testCatalogue1"
        return new CatalogueId(catalogueRepo, catalogueManifestFile, catalogueName)
    }

    def "get installations for all installationIds"() {
        given: "a no catalogueId"
        Optional<CatalogueId> catalogueId = Optional.empty()

        and: "a list of installation ids to search for"
        def installationIds = [1l,2l]

        and: "installations for each id"
        def account1 = Mock(Account)
        def installation1 = Mock(Installation)
        installation1.getAccount() >> account1
        def mappedInstallation1 = Mock(spectacular.backend.api.model.Installation)

        def account2 = Mock(Account)
        def installation2 = Mock(Installation)
        installation2.getAccount() >> account2
        def mappedInstallation2 = Mock(spectacular.backend.api.model.Installation)

        when: "the searching for installations with the catalogue id"
        def result = installationService.getInstallations(installationIds, catalogueId)

        then: "the github app rest api is called for each installation id"
        1 * appApiClient.getAppInstallation(installationIds.get(0).toString()) >> installation1
        1 * appApiClient.getAppInstallation(installationIds.get(1).toString()) >> installation2

        and: "both installations are  mapped"
        1 * installationMapper.mapInstallation(installation1) >> mappedInstallation1
        1 * installationMapper.mapInstallation(installation2) >> mappedInstallation2

        and: "both installations are returned"
        result.installations.size() == 2
        result.installations.get(0) == mappedInstallation1
        result.installations.get(1) == mappedInstallation2
    }

    def "get installation for a given catalogueId and list of installationIds"() {
        given: "a catalogueId"
        def catalogueId = Optional.of(aCatalogueId())

        and: "a list of installation ids to search for"
        def installationIds = [1l,2l]

        and: "installations for each id"
        def account1 = Mock(Account)
        def installation1 = Mock(Installation)
        installation1.getAccount() >> account1

        def account2 = Mock(Account)
        def installation2 = Mock(Installation)
        installation2.getAccount() >> account2
        def mappedInstallation = Mock(spectacular.backend.api.model.Installation)

        and: "only one installation has the same owner name as the Catalogue's Repo"
        account1.getLogin() >> "different"
        account2.getLogin() >> "test-owner"

        when: "the searching for installations with the catalogue id"
        def result = installationService.getInstallations(installationIds, catalogueId)

        then: "the github app rest api is called for each installation id"
        1 * appApiClient.getAppInstallation(installationIds.get(0).toString()) >> installation1
        1 * appApiClient.getAppInstallation(installationIds.get(1).toString()) >> installation2

        and: "only the matching installation is mapped"
        1 * installationMapper.mapInstallation(installation2) >> mappedInstallation

        and: "only the matching installation is returned"
        result.installations.size() == 1
        result.installations.get(0) == mappedInstallation
    }
}
