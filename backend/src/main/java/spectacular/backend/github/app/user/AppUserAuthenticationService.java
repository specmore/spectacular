package spectacular.backend.github.app.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppUserAuthenticationService {
  private static final Logger logger = LoggerFactory.getLogger(AppUserAuthenticationService.class);

  private final String clientId;
  private final String clientSecret;
  private final AppUserApiClient appUserApiClient;

  /**
   * A service for authenticating GitHub Users for a GitHub App.
   *
   * @param clientId a config value of the Client Id for the GitHub app representing this application instance
   * @param appUserApiClient
   */
  public AppUserAuthenticationService(@Value("${github.api.app.client-id}") String clientId,
                                      @Value("${github.api.app.client-secret}") String clientSecret,
                                      AppUserApiClient appUserApiClient) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.appUserApiClient = appUserApiClient;
  }

  public CreateUserSessionResult createUserSession(String code) {
    final var userAccessToken = this.appUserApiClient.requestUserAccessToken(this.clientId, this.clientSecret, code);

    return null;
  }

  public String getClientId() {
    return clientId;
  }
}
