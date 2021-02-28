package spectacular.backend.specevolution;

import org.springframework.stereotype.Component;
import spectacular.backend.cataloguemanifest.model.MainBranchConfig;
import spectacular.backend.cataloguemanifest.model.ReleaseBranchConfig;
import spectacular.backend.cataloguemanifest.model.ReleaseTagConfig;
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.RestApiClient;

@Component
public class SpecEvolutionConfigResolver {
  private final RestApiClient restApiClient;

  private static final String DEFAULT_MAIN_BRANCH_NAME = "main";

  public SpecEvolutionConfigResolver(RestApiClient restApiClient) {
    this.restApiClient = restApiClient;
  }

  /**
   * Resolves the provided SpecEvolutionConfig into a well constructed config object with defaults applied.
   *
   * @param specEvolutionConfig the provided SpecEvolutionConfig
   * @param specFileRepo the repo of the spec file the config is about
   * @return well constructed SpecEvolutionConfig object with defaults applied
   */
  public SpecEvolutionConfig resolveConfig(SpecEvolutionConfig specEvolutionConfig, RepositoryId specFileRepo) {
    var resolvedMainBranchConfig =  resolveMainBranchConfig(specEvolutionConfig, specFileRepo);
    var resolvedReleaseBranchConfig = resolvedReleaseBranchConfig(specEvolutionConfig);
    var resolvedReleaseTagConfig = resolvedReleaseTagConfig(specEvolutionConfig);
    return new SpecEvolutionConfig()
        .withMainBranchConfig(resolvedMainBranchConfig)
        .withReleaseBranchConfig(resolvedReleaseBranchConfig)
        .withReleaseTagConfig(resolvedReleaseTagConfig);
  }

  private MainBranchConfig resolveMainBranchConfig(SpecEvolutionConfig specEvolutionConfig, RepositoryId specFileRepo) {
    String mainBranchName = null;

    if (specEvolutionConfig != null &&
        specEvolutionConfig.getMainBranchConfig() != null &&
        specEvolutionConfig.getMainBranchConfig().getBranchName() != null) {
      mainBranchName = specEvolutionConfig.getMainBranchConfig().getBranchName();
    } else {
      var repo = restApiClient.getRepository(specFileRepo);
      var defaultBranchName = repo.getDefault_branch();
      if (defaultBranchName != null && !defaultBranchName.isBlank()) {
        mainBranchName = defaultBranchName;
      } else {
        mainBranchName = DEFAULT_MAIN_BRANCH_NAME;
      }
    }

    return new MainBranchConfig().withBranchName(mainBranchName);
  }

  private ReleaseBranchConfig resolvedReleaseBranchConfig(SpecEvolutionConfig specEvolutionConfig) {
    String branchPrefix = null;

    if (specEvolutionConfig != null &&
        specEvolutionConfig.getReleaseBranchConfig() != null &&
        specEvolutionConfig.getReleaseBranchConfig().getBranchPrefix() != null) {
      branchPrefix = specEvolutionConfig.getReleaseBranchConfig().getBranchPrefix();
    }

    return new ReleaseBranchConfig().withBranchPrefix(branchPrefix);
  }

  private ReleaseTagConfig resolvedReleaseTagConfig(SpecEvolutionConfig specEvolutionConfig) {
    String tagPrefix = null;

    if (specEvolutionConfig != null &&
        specEvolutionConfig.getReleaseTagConfig() != null &&
        specEvolutionConfig.getReleaseTagConfig().getTagPrefix() != null) {
      tagPrefix = specEvolutionConfig.getReleaseTagConfig().getTagPrefix();
    }

    return new ReleaseTagConfig().withTagPrefix(tagPrefix);
  }
}
