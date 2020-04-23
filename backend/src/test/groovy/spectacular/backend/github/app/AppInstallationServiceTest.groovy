package spectacular.backend.github.app

import spectacular.backend.github.domain.AccessTokenResult
import spock.lang.Specification

import java.time.ZonedDateTime

class AppInstallationServiceTest extends Specification {
    def appApiClient = Mock(AppApiClient)
    def appInstallationAccessTokenStore = Mock(AppInstallationAccessTokenStore)
    def appInstallationService = new AppInstallationService(appApiClient, appInstallationAccessTokenStore)

    def "GetAccessTokenForInstallation with no existing token for installation"() {
        given: "an app installation with no existing access token"
        def installationId = "98"
        and: "a new, 10 minute access token"
        def newAccessToken = new AccessTokenResult("test-token", ZonedDateTime.now().plusMinutes(10))

        when: "an access token is requested for an installation"
        def accessToken = appInstallationService.getAccessTokenForInstallation(installationId)

        then: "a new access token is created using the GitHub api"
        1 * appApiClient.createNewAppInstallationAccessToken(installationId) >> newAccessToken

        and: "the new access token is stored"
        1 * appInstallationAccessTokenStore.putAccessTokenForInstallation(newAccessToken, installationId)

        and: "the new valid token is returned"
        accessToken == newAccessToken
    }

    def "GetAccessTokenForInstallation with existing valid tokens"() {
        given: "an app installation with a valid existing access token"
        def installationId = "97"
        def validAccessToken = new AccessTokenResult("test-token", ZonedDateTime.now().plusMinutes(5))
        appInstallationAccessTokenStore.getAccessTokenForInstallation(installationId) >> validAccessToken

        when: "an access token is requested for an installation"
        def accessToken = appInstallationService.getAccessTokenForInstallation(installationId)

        then: "no new access token is created using the GitHub api"
        0 * appApiClient.createNewAppInstallationAccessToken(installationId)

        and: "the existing valid token is returned"
        accessToken == validAccessToken
    }

    def "GetAccessTokenForInstallation with existing invalid tokens"() {
        given: "an app installation with a invalid existing access token"
        def installationId = "96"
        def invalidAccessToken = new AccessTokenResult("test-token-old", ZonedDateTime.now().plusSeconds(15))
        appInstallationAccessTokenStore.getAccessTokenForInstallation(installationId) >> invalidAccessToken
        and: "a new, 10 minute access token"
        def newAccessToken = new AccessTokenResult("new-test-token", ZonedDateTime.now().plusMinutes(10))

        when: "an access token is requested for an installation"
        def accessToken = appInstallationService.getAccessTokenForInstallation(installationId)

        then: "a new access token is created using the GitHub api"
        1 * appApiClient.createNewAppInstallationAccessToken(installationId) >> newAccessToken

        and: "the new access token is stored"
        1 * appInstallationAccessTokenStore.putAccessTokenForInstallation(newAccessToken, installationId)

        and: "the new valid token is returned"
        accessToken == newAccessToken
    }
}
