package spectacular.backend.specevolution;

import org.springframework.stereotype.Service;
import spectacular.backend.api.model.SpecEvolution;
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig;
import spectacular.backend.common.RepositoryId;

@Service
public class SpecEvolutionService {
  private final SpecEvolutionDataExtractor specEvolutionDataExtractor;
  private final SpecEvolutionBuilder specEvolutionBuilder;
  private final SpecEvolutionConfigResolver specEvolutionConfigResolver;

  /**
   * A service for generating Spec Evolutions.
   * @param specEvolutionDataExtractor a data provider for the raw git data
   * @param specEvolutionBuilder a builder to convert the raw git data
   * @param specEvolutionConfigResolver a helper for defaulting missing config
   */
  public SpecEvolutionService(SpecEvolutionDataExtractor specEvolutionDataExtractor,
                              SpecEvolutionBuilder specEvolutionBuilder,
                              SpecEvolutionConfigResolver specEvolutionConfigResolver) {
    this.specEvolutionDataExtractor = specEvolutionDataExtractor;
    this.specEvolutionBuilder = specEvolutionBuilder;
    this.specEvolutionConfigResolver = specEvolutionConfigResolver;
  }

  /**
   * Gets the Spec Evolution view of an interface's spec file.
   * @param interfaceName the name of the interface in a catalogue
   * @param specEvolutionConfig the config to use when building the view
   * @param specFileRepo the repository the spec file is in
   * @param specFilePath the path to the spec file
   * @return a Spec Evolution view of the spec file's git data
   */
  public SpecEvolution getSpecEvolution(String interfaceName,
                                        SpecEvolutionConfig specEvolutionConfig,
                                        RepositoryId specFileRepo,
                                        String specFilePath) {
    var resolvedConfig = specEvolutionConfigResolver.resolveConfig(specEvolutionConfig, specFileRepo);

    var mainBranch = specEvolutionDataExtractor.getMainBranchAccordingToConfig(resolvedConfig, specFileRepo, specFilePath);

    var tags = specEvolutionDataExtractor.getRepoTagsAccordingToConfig(resolvedConfig, specFileRepo);

    var branches = specEvolutionDataExtractor.getReleaseBranchesAccordingToConfig(resolvedConfig, specFileRepo, specFilePath);

    var specEvolutionDataResult = new SpecEvolutionData(mainBranch, tags, branches, resolvedConfig);

    return specEvolutionBuilder.generateSpecEvolution(interfaceName, specFileRepo, specFilePath, specEvolutionDataResult);
  }
}
