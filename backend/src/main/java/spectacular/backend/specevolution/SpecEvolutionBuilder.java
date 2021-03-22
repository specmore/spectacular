package spectacular.backend.specevolution;

import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import spectacular.backend.api.model.EvolutionBranch;
import spectacular.backend.api.model.EvolutionItem;
import spectacular.backend.api.model.SpecEvolution;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.domain.Tag;
import spectacular.backend.github.pullrequests.PullRequest;
import spectacular.backend.github.refs.BranchRef;
import spectacular.backend.github.refs.TagRef;

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

    var tags = specEvolutionData.getTags();

    if (specEvolutionData.getMainBranch().isPresent()) {
      var mainBranch = specEvolutionData.getMainBranch().get().getBranch();
      var mainBranchPRs = specEvolutionData.getMainBranch().get().getAssociatedPullRequest();
      var mainEvolutionBranch = generateEvolutionBranch(mainBranch, tags, mainBranchPRs, specFileRepo);
      specEvolution.setMain(mainEvolutionBranch);
    }

    var releaseBranches = specEvolutionData.getReleaseBranches().stream()
        .map(branchData -> this.generateEvolutionBranch(branchData.getBranch(), tags, branchData.getAssociatedPullRequest(), specFileRepo))
        .collect(Collectors.toList());

    specEvolution.setReleases(releaseBranches);

    return specEvolution;
  }

  private EvolutionBranch generateEvolutionBranch(BranchRef branchRef,
                                                  Collection<TagRef> tags,
                                                  Collection<PullRequest> pullRequests,
                                                  RepositoryId specFileRepo) {
    var branchName = branchRef.getName();
    var evolutionItems = this.evolutionBranchBuilder.generateEvolutionItems(
        specFileRepo,
        branchRef,
        tags,
        pullRequests);

    var usedTags = evolutionItems.stream()
        .flatMap(evolutionItem -> evolutionItem.getTags().stream())
        .collect(Collectors.toList());

    tags.removeIf(tag -> usedTags.contains(tag.getName()));

    return new EvolutionBranch().branchName(branchName).evolutionItems(evolutionItems);
  }
}
