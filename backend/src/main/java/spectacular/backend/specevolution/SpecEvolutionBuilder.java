package spectacular.backend.specevolution;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import spectacular.backend.api.model.EvolutionBranch;
import spectacular.backend.api.model.SpecEvolution;
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.domain.Tag;
import spectacular.backend.github.refs.BranchRef;
import spectacular.backend.github.refs.RefRepository;

@Service
public class SpecEvolutionBuilder {
  private final EvolutionBranchBuilder evolutionBranchBuilder;

  /**
   * A builder that creates Spec Evolution objects with evolution branches.
   * @param evolutionBranchBuilder an evolution branch builder
   */
  public SpecEvolutionBuilder(EvolutionBranchBuilder evolutionBranchBuilder) {
    this.evolutionBranchBuilder = evolutionBranchBuilder;
  }

  /**
   * Generates a Spec Evolution for a given interface and its spec evolution config from a git repository.
   * @param interfaceName the name of the interface in the catalogue manifest
   * @param specFileRepo the specFileRepo
   * @param specFilePath the specFilePath
   * @param specEvolutionData spec evolution git history data pulled from the repository
   * @return a spec evolution representation of the git history of the spec file
   */
  public SpecEvolution generateSpecEvolution(String interfaceName,
                                             RepositoryId specFileRepo,
                                             String specFilePath,
                                             SpecEvolutionData specEvolutionData) {
    var specEvolution = new SpecEvolution()
        .interfaceName(interfaceName)
        .configUsed(specEvolutionData.getSpecEvolutionConfig());

    if (specEvolutionData.getMainBranch().isPresent()) {
      var mainBranch = generateEvolutionBranch(specEvolutionData.getMainBranch().get(), specEvolutionData.getTags(), specFileRepo);
      specEvolution.setMain(mainBranch);
    }

    var releaseBranches = specEvolutionData.getReleaseBranches().stream()
        .map(branchRef -> this.generateEvolutionBranch(branchRef, specEvolutionData.getTags(), specFileRepo))
        .collect(Collectors.toList());

    specEvolution.setReleases(releaseBranches);

    return specEvolution;
  }

  private EvolutionBranch generateEvolutionBranch(BranchRef branchRef, List<Tag> tags, RepositoryId specFileRepo) {
    var branchName = branchRef.getName();
    var tagEvolutionItems = this.evolutionBranchBuilder.generateEvolutionItems(specFileRepo,
        branchName,
        tags);

    return new EvolutionBranch().branchName(branchName).evolutionItems(tagEvolutionItems);
  }
}
