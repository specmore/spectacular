package spectacular.backend.specevolution;

import java.util.List;
import java.util.Optional;
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig;
import spectacular.backend.github.domain.Tag;
import spectacular.backend.github.refs.TagRef;

public class SpecEvolutionData {
  private final Optional<BranchData> mainBranch;
  private final List<TagRef> tags;
  private final List<BranchData> releaseBranches;
  private final SpecEvolutionConfig specEvolutionConfig;

  /**
   * An object with all the data needed from a git service to build up an spec evolution view.
   * @param mainBranch the main branch data
   * @param tags all the tags on the repository that matches the config
   * @param releaseBranches all the release branches that match the config
   * @param specEvolutionConfig the original config used to pull the correct git data
   */
  public SpecEvolutionData(Optional<BranchData> mainBranch,
                           List<TagRef> tags,
                           List<BranchData> releaseBranches,
                           SpecEvolutionConfig specEvolutionConfig) {
    this.mainBranch = mainBranch;
    this.tags = tags;
    this.releaseBranches = releaseBranches;
    this.specEvolutionConfig = specEvolutionConfig;
  }

  public List<TagRef> getTags() {
    return tags;
  }

  public List<BranchData> getReleaseBranches() {
    return releaseBranches;
  }

  public SpecEvolutionConfig getSpecEvolutionConfig() {
    return specEvolutionConfig;
  }

  public Optional<BranchData> getMainBranch() {
    return mainBranch;
  }
}
