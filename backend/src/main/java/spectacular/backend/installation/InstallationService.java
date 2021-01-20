package spectacular.backend.installation;

import org.springframework.stereotype.Service;
import spectacular.backend.api.model.Installation;
import spectacular.backend.github.app.AppApiClient;
import spectacular.backend.github.app.AppInstallationContextProvider;

@Service
public class InstallationService {
  private final AppApiClient appApiClient;
  private final AppInstallationContextProvider appInstallationContextProvider;
  private final InstallationMapper installationMapper;


  /**
   * A service for retrieving information about an installation of Spectacular on a source control platform.
   * @param appApiClient an API client for the source control platform
   * @param appInstallationContextProvider a service that provides additional contexts about the installation
   * @param installationMapper a mapper to translate the API responses into a generic installation object
   */
  public InstallationService(AppApiClient appApiClient, AppInstallationContextProvider appInstallationContextProvider,
                             InstallationMapper installationMapper) {
    this.appApiClient = appApiClient;
    this.appInstallationContextProvider = appInstallationContextProvider;
    this.installationMapper = installationMapper;
  }

  public Installation getCurrentInstallation() {
    var gitHubInstallation = appApiClient.getAppInstallation(appInstallationContextProvider.getInstallationId());
    return installationMapper.mapInstallation(gitHubInstallation);
  }
}
