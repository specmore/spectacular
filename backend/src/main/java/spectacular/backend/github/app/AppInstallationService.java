package spectacular.backend.github.app;

import java.time.ZonedDateTime;
import org.springframework.stereotype.Service;
import spectacular.backend.github.domain.AccessTokenResult;

@Service
public class AppInstallationService {
  private final AppApiClient appApiClient;
  private final AppInstallationAccessTokenStore appInstallationAccessTokenStore;

  public AppInstallationService(AppApiClient appApiClient,
                                AppInstallationAccessTokenStore appInstallationAccessTokenStore) {
    this.appApiClient = appApiClient;
    this.appInstallationAccessTokenStore = appInstallationAccessTokenStore;
  }

  /**
   * Get a GitHub API access token for the given installation.
   *
   * @param installationId the installation id of the installation of this GitHub app for a given user or organisation
   * @return a new AccessTokenResult object with the actual access token and other details like expiration date
   */
  public AccessTokenResult getAccessTokenForInstallation(String installationId) {
    var accessToken = appInstallationAccessTokenStore.getAccessTokenForInstallation(installationId);

    if (accessToken == null || accessToken.getExpirationDateTime().isBefore(ZonedDateTime.now().plusSeconds(30))) {
      accessToken = this.appApiClient.requestNewAppInstallationAccessToken(installationId);
      appInstallationAccessTokenStore.putAccessTokenForInstallation(accessToken, installationId);
    }

    return accessToken;
  }
}
