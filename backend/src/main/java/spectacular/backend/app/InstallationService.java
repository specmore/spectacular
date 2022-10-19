package spectacular.backend.app;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import spectacular.backend.api.model.GetInstallationsResult;
import spectacular.backend.api.model.Installation;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.github.app.AppApiClient;

@Service
public class InstallationService {
  private final AppApiClient appApiClient;
  private final InstallationMapper installationMapper;


  /**
   * A service for retrieving information about an installation of Spectacular on a source control platform.
   * @param appApiClient an API client for the source control platform
   * @param installationMapper a mapper to translate the API responses into a generic installation object
   */
  public InstallationService(AppApiClient appApiClient,
                             InstallationMapper installationMapper) {
    this.appApiClient = appApiClient;
    this.installationMapper = installationMapper;
  }

  public Installation getInstallation(Integer installationId) {
    var gitHubInstallation = appApiClient.getAppInstallation(installationId.toString());
    return installationMapper.mapInstallation(gitHubInstallation);
  }

  /**
   * Gets the details of all installations of the app for a given set of installation ids.
   * @param installationIds that are to be used to retrieve the installation details
   * @return a GetInstallationsResult containing all the retrieved installations
   */
  public GetInstallationsResult getInstallations(List<Long> installationIds, Optional<CatalogueId> catalogueId) {
    final var installations = installationIds.stream()
        .map(installationId -> this.appApiClient.getAppInstallation(installationId.toString()))
        .filter(installation -> catalogueId.isEmpty() ||
            Objects.equals(catalogueId.get().getRepositoryId().getOwner(), installation.getAccount().getLogin()))
        .map(this.installationMapper::mapInstallation)
        .collect(Collectors.toList());
    return new GetInstallationsResult().installations(installations);
  }
}
