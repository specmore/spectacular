package spectacular.backend.github.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppUserAuthenticationService {
  private static final Logger logger = LoggerFactory.getLogger(AppUserAuthenticationService.class);
  private final String clientId;

  /**
   * A service for authenticating GitHub Users for a GitHub App.
   *
   * @param clientId a config value of the Client Id for the GitHub app representing this application instance
   */
  public AppUserAuthenticationService(@Value("${github.api.app.client-id}") String clientId) {
    this.clientId = clientId;
  }

  public String getClientId() {
    return clientId;
  }
}
