package spectacular.backend.installation;

import org.springframework.stereotype.Service;
import spectacular.backend.github.app.AppApiClient;
import spectacular.backend.github.app.AppInstallationContextProvider;
import spectacular.backend.github.domain.Installation;

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
