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

  public SpecEvolutionDataExtractor(RefRepository refRepository) {
    this.refRepository = refRepository;
  }

  /**
   * Gets the Tag data needed build a Spec Evolution view of a Spec File from the data stored in a git service.
   * @param specEvolutionConfig the config about what tag data pull
   * @param specFileRepo the git repo of the file that the spec evolution is about
   * @return the tag data
   */
  public List<Tag> getRepoTagsAccordingToConfig(SpecEvolutionConfig specEvolutionConfig, RepositoryId specFileRepo) {
    final String tagPrefix = specEvolutionConfig.getReleaseTagConfig().getTagPrefix();

    return this.refRepository.getTagsForRepo(specFileRepo, tagPrefix);
  }

  /**
   * Gets the Release Branch data needed build a Spec Evolution view of a Spec File from the data stored in a git service.
   * @param specEvolutionConfig the config about what branch data pull
   * @param specFileRepo the git repo of the file that the spec evolution is about
   * @param specFilePath the path to the file that the spec evolution is about
   * @return the release branch data
   */
  public List<BranchRef> getReleaseBranchesAccordingToConfig(SpecEvolutionConfig specEvolutionConfig,
                                                             RepositoryId specFileRepo,
                                                             String specFilePath) {

    if (specEvolutionConfig.getReleaseBranchConfig().getBranchPrefix() != null) {
      var branchPrefix = specEvolutionConfig.getReleaseBranchConfig().getBranchPrefix();
      return this.refRepository.getBranchesForRepo(specFileRepo, branchPrefix, specFilePath);
    }

    return Collections.emptyList();
  }

  /**
   * Gets the Main Branch data needed build a Spec Evolution view of a Spec File from the data stored in a git service.
   * @param specEvolutionConfig the config about what branch data pull
   * @param specFileRepo the git repo of the file that the spec evolution is about
   * @param specFilePath the path to the file that the spec evolution is about
   * @return the main branch data
   */
  public Optional<BranchRef> getMainBranchAccordingToConfig(SpecEvolutionConfig specEvolutionConfig,
                                                             RepositoryId specFileRepo,
                                                             String specFilePath) {
    final String mainBranchName = specEvolutionConfig.getMainBranchConfig().getBranchName();

    var branches = this.refRepository.getBranchesForRepo(specFileRepo, mainBranchName, specFilePath);

    return branches.stream().filter(branchRef -> branchRef.getName().equalsIgnoreCase(mainBranchName)).findFirst();
  }
}
