package spectacular.backend.specevolution;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.domain.Tag;
import spectacular.backend.github.refs.BranchRef;
import spectacular.backend.github.refs.RefRepository;

@Component
public class SpecEvolutionDataExtractor {
  private final RefRepository refRepository;

  private static final String DEFAULT_MAIN_BRANCH_NAME = "main";

  public SpecEvolutionDataExtractor(RefRepository refRepository) {
    this.refRepository = refRepository;
  }

  /**
   * Gets the data needed build a Spec Evolution view of a Spec File from the data stored in a git service.
   * @param specFileRepo the git repo the Spec File is located in
   * @param specFilePath the path to the Spec File in the repo
   * @param specEvolutionConfig the config supplied to pull the correct git data
   * @return the extracted SpecEvolutionData
   */
  public SpecEvolutionData getEvolutionDataForSpecFile(RepositoryId specFileRepo,
                                                       String specFilePath,
                                                       SpecEvolutionConfig specEvolutionConfig) {
    var mainBranch = getMainBranchAccordingToConfig(specEvolutionConfig, specFileRepo, specFilePath);

    var tags = getRepoTagsAccordingToConfig(specEvolutionConfig, specFileRepo);

    var branches = getBranchesAccordingToConfig(specEvolutionConfig, specFileRepo, specFilePath);

    return new SpecEvolutionData(mainBranch, tags, branches, specEvolutionConfig);
  }

  private List<Tag> getRepoTagsAccordingToConfig(SpecEvolutionConfig specEvolutionConfig, RepositoryId specFileRepo) {
    String tagPrefix = null;

    if (specEvolutionConfig != null &&
        specEvolutionConfig.getReleaseTagConfig() != null &&
        specEvolutionConfig.getReleaseTagConfig().getTagPrefix() != null) {
      tagPrefix = specEvolutionConfig.getReleaseTagConfig().getTagPrefix();
    }

    return this.refRepository.getTagsForRepo(specFileRepo, tagPrefix);
  }

  private List<BranchRef> getBranchesAccordingToConfig(SpecEvolutionConfig specEvolutionConfig,
                                                       RepositoryId specFileRepo,
                                                       String specFilePath) {

    if (specEvolutionConfig != null &&
        specEvolutionConfig.getReleaseBranchConfig() != null &&
        specEvolutionConfig.getReleaseBranchConfig().getBranchPrefix() != null) {
      var branchPrefix = specEvolutionConfig.getReleaseBranchConfig().getBranchPrefix();
      return this.refRepository.getBranchesForRepo(specFileRepo, branchPrefix, specFilePath);
    }

    return Collections.emptyList();
  }

  private Optional<BranchRef> getMainBranchAccordingToConfig(SpecEvolutionConfig specEvolutionConfig,
                                                             RepositoryId specFileRepo,
                                                             String specFilePath) {
    String configMainBranchName = null;

    if (specEvolutionConfig != null &&
        specEvolutionConfig.getMainBranchConfig() != null &&
        specEvolutionConfig.getMainBranchConfig().getBranchName() != null) {
      configMainBranchName = specEvolutionConfig.getMainBranchConfig().getBranchName();
    }

    final String mainBranchName = configMainBranchName != null ? configMainBranchName : DEFAULT_MAIN_BRANCH_NAME;

    var branches = this.refRepository.getBranchesForRepo(specFileRepo, mainBranchName, specFilePath);

    return branches.stream().filter(branchRef -> branchRef.getName().equalsIgnoreCase(mainBranchName)).findFirst();
  }
}
