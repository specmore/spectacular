package spectacular.backend.specevolution;

import org.springframework.stereotype.Service;
import spectacular.backend.api.model.SpecEvolution;
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig;
import spectacular.backend.common.RepositoryId;

@Service
public class SpecEvolutionService {
  private final SpecEvolutionDataExtractor specEvolutionDataExtractor;
  private final SpecEvolutionBuilder specEvolutionBuilder;

  public SpecEvolutionService(SpecEvolutionDataExtractor specEvolutionDataExtractor,
                              SpecEvolutionBuilder specEvolutionBuilder) {
    this.specEvolutionDataExtractor = specEvolutionDataExtractor;
    this.specEvolutionBuilder = specEvolutionBuilder;
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
    var specEvolutionDataResult = specEvolutionDataExtractor.getEvolutionDataForSpecFile(specFileRepo, specFilePath, specEvolutionConfig);

    return specEvolutionBuilder.generateSpecEvolution(interfaceName, specFileRepo, specFilePath, specEvolutionDataResult);
  }
}
