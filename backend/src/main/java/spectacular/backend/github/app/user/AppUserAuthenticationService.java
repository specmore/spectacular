package spectacular.backend.github.app.user;

import com.nimbusds.jose.JOSEException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spectacular.backend.api.model.UserDetails;
import spectacular.backend.app.UserSessionTokenService;
import spectacular.backend.github.domain.UserAccessTokenRequest;

@Service
public class AppUserAuthenticationService {
  private static final Logger logger = LoggerFactory.getLogger(AppUserAuthenticationService.class);

  private final String clientId;
  private final String clientSecret;
  private final AppUserApiClient appUserApiClient;
  private final AppOAuthApiClient appOAuthApiClient;
  private final UserSessionTokenService userSessionTokenService;

  /**
   * A service for orchestrating the authentication of GitHub Users for a GitHub App.
   * @param clientId a config value of the Client ID for the GitHub App representing this application instance
   * @param appUserApiClient the GitHub API client for making requests in the context of a User
   * @param userSessionTokenService a service for generating user session tokens after a successful authentication
   */
  public AppUserAuthenticationService(@Value("${github.api.app.client-id}") String clientId,
                                      @Value("${github.api.app.client-secret}") String clientSecret,
                                      AppUserApiClient appUserApiClient,
                                      AppOAuthApiClient appOAuthApiClient,
                                      UserSessionTokenService userSessionTokenService) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.appUserApiClient = appUserApiClient;
    this.appOAuthApiClient = appOAuthApiClient;
    this.userSessionTokenService = userSessionTokenService;
  }

  /**
   * Orchestrates the completion of a user authentication workflow and creation of a user session.
   * @param code the code given back by the GitHub App OAuth workflow with which the final OAuth workflow step can be completed.
   * @return the CreateUserSessionResult with the session token and user details
   */
  public CreateUserSessionResult createUserSession(String code) {
    final var userAccessTokenRequest = new UserAccessTokenRequest(this.clientId, this.clientSecret, code);
    final var userAccessTokenResult = this.appOAuthApiClient.requestUserAccessToken(userAccessTokenRequest);

    if (userAccessTokenResult.getAccessToken() == null) {
      // Something went wrong.
      throw new OAuthUserAccessTokenErrorException(code);
    }

    var user = this.appUserApiClient.getUser(userAccessTokenResult.getAccessToken());
    var installations = this.appUserApiClient.getInstallationsAccessibleByUser(userAccessTokenResult.getAccessToken());
    var userDetails = new UserDetails()
        .username(user.getLogin())
        .fullName(user.getName())
        .profileImageUrl(user.getAvatarUrl());

    String userSessionToken = null;
    try {
      userSessionToken = this.userSessionTokenService.generateUserSessionToken(userDetails, installations);
    } catch (JOSEException e) {
      logger.error("An error occurred while generating a new User Session JWT for User: '{}'.", userDetails.getUsername(), e);
    }

    return new CreateUserSessionResult(userSessionToken, userDetails);
  }

  public String getClientId() {
    return clientId;
  }
}
