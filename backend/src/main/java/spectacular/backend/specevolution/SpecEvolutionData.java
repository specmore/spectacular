package spectacular.backend.specevolution;

import java.util.List;
import java.util.Optional;
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig;
import spectacular.backend.github.domain.Tag;
import spectacular.backend.github.refs.BranchRef;

public class SpecEvolutionData {
  private final Optional<BranchRef> mainBranch;
  private final List<Tag> tags;
  private final List<BranchRef> branches;
  private final SpecEvolutionConfig specEvolutionConfig;

  /**
   * An object with all the data needed from a git service to build up an spec evolution view.
   * @param mainBranch the main branch data
   * @param tags all the tags on the repository that matches the config
   * @param branches all the branches that match the config
   * @param specEvolutionConfig the original config used to pull the correct git data
   */
  public SpecEvolutionData(Optional<BranchRef> mainBranch,
                           List<Tag> tags,
                           List<BranchRef> branches,
                           SpecEvolutionConfig specEvolutionConfig) {
    this.mainBranch = mainBranch;
    this.tags = tags;
    this.branches = branches;
    this.specEvolutionConfig = specEvolutionConfig;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public List<BranchRef> getBranches() {
    return branches;
  }

  public SpecEvolutionConfig getSpecEvolutionConfig() {
    return specEvolutionConfig;
  }

  public Optional<BranchRef> getMainBranch() {
    return mainBranch;
  }
}
