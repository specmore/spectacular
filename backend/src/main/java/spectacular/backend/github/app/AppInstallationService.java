package spectacular.backend.github.app;

import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class AppInstallationService {
    private final AppApiClient appApiClient;
    private final AppInstallationAccessTokenStore appInstallationAccessTokenStore;

    public AppInstallationService(AppApiClient appApiClient, AppInstallationAccessTokenStore appInstallationAccessTokenStore) {
        this.appApiClient = appApiClient;
        this.appInstallationAccessTokenStore = appInstallationAccessTokenStore;
    }

    public AccessTokenResult getAccessTokenForInstallation(String installationId) {
        var accessToken = appInstallationAccessTokenStore.getAccessTokenForInstallation(installationId);

        if (accessToken == null || accessToken.getExpirationDateTime().isBefore(ZonedDateTime.now().plusSeconds(30))) {
            accessToken = this.appApiClient.createNewAppInstallationAccessToken(installationId);
            appInstallationAccessTokenStore.putAccessTokenForInstallation(accessToken, installationId);
        }

        return accessToken;
    }
}
