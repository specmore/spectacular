package spectacular.backend.github.app.user

import spectacular.backend.app.UserSessionTokenService
import spectacular.backend.github.domain.Account
import spectacular.backend.github.domain.GetInstallationsResult
import spectacular.backend.github.domain.UserAccessTokenResult
import spock.lang.Specification

class AppUserAuthenticationServiceTest extends Specification {
    def clientId = "testClientId"
    def clientSecret = "testClientSecret"
    def appUserApiClient = Mock(AppUserApiClient)
    def appOAuthApiClient = Mock(AppOAuthApiClient)
    def userSessionTokenService = Mock(UserSessionTokenService)
    def appUserAuthenticationService = new AppUserAuthenticationService(clientId, clientSecret, appUserApiClient, appOAuthApiClient, userSessionTokenService)

    def "CreateUserSession successfully generates a user session token for a given OAuth workflow code"() {
        given: "a valid code is received from a successful GitHub OAuth workflow"
        def oAuthCode = "validOAuthCode"

        and: "a valid GitHub user access token will be provided for the GitHub app's client id & secret and the OAuth code"
        def userAccessToken = "validAccessToken"
        def userAccessTokenResult = Mock(UserAccessTokenResult)
        userAccessTokenResult.getAccessToken() >> userAccessToken

        and: "the GitHub user has account detail"
        def userAccountDetails = Mock(Account)

        and: "the is able to access some installations of the GitHub App"
        def installations = Mock(GetInstallationsResult)

        and: "a user session JWT token containing the user's details"
        def userSessionToken = "aUserSessionToken"

        when: "the createUserSessions function is called with the OAuth code"
        def result = appUserAuthenticationService.createUserSession(oAuthCode)

        then: "a user access token is requested"
        1 * appOAuthApiClient.requestUserAccessToken({ it.getCode() == oAuthCode }) >> userAccessTokenResult

        and: "the user details are requested using the access token"
        1 * appUserApiClient.getUser(userAccessToken) >> userAccountDetails

        and: "the installations accessible by the user are requested using the access token"
        1 * appUserApiClient.getInstallationsAccessibleByUser(userAccessToken) >> installations

        and: "a user session token is generated"
        1 * userSessionTokenService.generateUserSessionToken(_, installations) >> userSessionToken

        and: "a user session is successfully returned"
        result
        result.userSessionToken == userSessionToken
    }

    def "CreateUserSession throws an error if GitHub doesn't generate a user access token"() {
        given: "an invalid code is received from a successful GitHub OAuth workflow"
        def oAuthCode = "invalidOAuthCode"

        and: "no GitHub user access token is provided for the GitHub app's client id & secret and the OAuth code"
        def userAccessTokenResult = Mock(UserAccessTokenResult)
        userAccessTokenResult.getAccessToken() >> null

        when: "the createUserSessions function is called with the OAuth code"
        def result = appUserAuthenticationService.createUserSession(oAuthCode)

        then: "a user access token is requested"
        1 * appOAuthApiClient.requestUserAccessToken({ it.getCode() == oAuthCode }) >> userAccessTokenResult

        and: "no user details are requested"
        0 * appUserApiClient.getUser(_)

        and: "no installations accessible by the user are requested"
        0 * appUserApiClient.getInstallationsAccessibleByUser(_)

        and: "no user session token is generated"
        0 * userSessionTokenService.generateUserSessionToken(_, _)

        and: "a OAuthUserAccessTokenErrorException is throw"
        thrown(OAuthUserAccessTokenErrorException)
    }
}
