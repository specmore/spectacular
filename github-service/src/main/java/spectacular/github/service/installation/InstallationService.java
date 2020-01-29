package spectacular.github.service.installation;

import org.springframework.stereotype.Service;
import spectacular.github.service.github.app.AppApiClient;
import spectacular.github.service.github.app.AppInstallationContextProvider;
import spectacular.github.service.github.app.Installation;

@Service
public class InstallationService {
    private final AppApiClient appApiClient;
    private final AppInstallationContextProvider appInstallationContextProvider;

    public InstallationService(AppApiClient appApiClient, AppInstallationContextProvider appInstallationContextProvider) {
        this.appApiClient = appApiClient;
        this.appInstallationContextProvider = appInstallationContextProvider;
    }

    public Installation getCurrentInstallation() {
        return appApiClient.getAppInstallation(appInstallationContextProvider.getInstallationId());
    }
}
