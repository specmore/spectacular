package spectacular.github.service.github.app;

import java.util.HashMap;
import java.util.Map;

public class AppInstallationAccessTokenStore {
    private final Map<String, AccessTokenResult> accessTokenMap = new HashMap<>();

    public void putAccessTokenForInstallation(AccessTokenResult accessToken, String installationId) {
        accessTokenMap.put(installationId, accessToken);
    }

    public AccessTokenResult getAccessTokenForInstallation(String installationId) {
        return accessTokenMap.get(installationId);
    }
}
