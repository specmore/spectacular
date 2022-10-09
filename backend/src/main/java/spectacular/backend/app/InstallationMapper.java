package spectacular.backend.app;

import org.springframework.stereotype.Component;
import spectacular.backend.api.model.Installation;

/**
 * A mapper for translating installation objects from different version control platforms.
 */
@Component
public class InstallationMapper {

  /**
   * Creates a generic installation object from a GitHub installation.
   * @param gitHubInstallation an installation object returned by the GitHub API
   * @return an installation object
   */
  public Installation mapInstallation(spectacular.backend.github.domain.Installation gitHubInstallation) {
    return new Installation()
        .id(gitHubInstallation.getId())
        .owner(gitHubInstallation.getAccount().getLogin())
        .ownerImageUrl(gitHubInstallation.getAccount().getAvatarUrl());
  }
}
