package spectacular.backend.specevolution;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import spectacular.backend.api.model.EvolutionBranch;
import spectacular.backend.api.model.SpecEvolution;
import spectacular.backend.cataloguemanifest.model.Interface;
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.domain.Tag;

@Service
public class SpecEvolutionBuilder {
  private final RestApiClient restApiClient;
  private final EvolutionBranchBuilder evolutionBranchBuilder;

  public SpecEvolutionBuilder(RestApiClient restApiClient, EvolutionBranchBuilder evolutionBranchBuilder) {
    this.restApiClient = restApiClient;
    this.evolutionBranchBuilder = evolutionBranchBuilder;
  }

  /**
   * Generates a Spec Evolution for a given interface and its spec evolution config from a git repository.
   * @param interfaceName the name of the interface in the catalogue manifest
   * @param specEvolutionConfig the config for determining the evolution of the spec file from the git repo
   * @param specFileRepo the specFileRepo
   * @param mainBranchName the name of the branch
   * @return a spec evolution representation of the git history of the spec file
   */
  public SpecEvolution generateSpecEvolution(String interfaceName,
                                             SpecEvolutionConfig specEvolutionConfig,
                                             RepositoryId specFileRepo,
                                             String mainBranchName) {
    var specEvolution = new SpecEvolution()
        .interfaceName(interfaceName)
        .configUsed(specEvolutionConfig);

    var matchingTags = getRepoTagsAccordingToConfig(specEvolutionConfig, specFileRepo);

    var mainBranchTagEvolutionItems = this.evolutionBranchBuilder.generateEvolutionItems(specFileRepo, mainBranchName, matchingTags);

    var mainBranch = new EvolutionBranch().branchName(mainBranchName).evolutionItems(mainBranchTagEvolutionItems);

    specEvolution.setMain(mainBranch);

    return specEvolution;
  }

  private List<Tag> getRepoTagsAccordingToConfig(SpecEvolutionConfig specEvolutionConfig, RepositoryId specFileRepo) {
    var tags = this.restApiClient.getRepositoryTags(specFileRepo);

    if (specEvolutionConfig != null &&
        specEvolutionConfig.getReleaseTagConfig() != null &&
        specEvolutionConfig.getReleaseTagConfig().getTagPrefix() != null) {
      var tagPrefix = specEvolutionConfig.getReleaseTagConfig().getTagPrefix();
      return tags.stream().filter(tag -> tag.getName().startsWith(tagPrefix)).collect(Collectors.toList());
    }

    return tags;
  }
}
