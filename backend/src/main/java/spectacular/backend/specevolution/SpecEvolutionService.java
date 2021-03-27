package spectacular.backend.specevolution;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import spectacular.backend.api.model.SpecEvolution;
import spectacular.backend.cataloguemanifest.SpecFileRepositoryResolver;
import spectacular.backend.cataloguemanifest.model.Catalogue;
import spectacular.backend.cataloguemanifest.model.Interface;
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig;
import spectacular.backend.common.CatalogueId;
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
   * Gets a list of SpecEvolutions for all the interfaces in a given interface catalogue.
   * @param catalogue the catalogue manifest with a list of interfaces
   * @param catalogueId the unique identifier of the catalogue
   * @return a list of SpecEvolutions
   */
  public List<SpecEvolution> getSpecEvolutionsFor(Catalogue catalogue, CatalogueId catalogueId) {
    return catalogue.getInterfaces().getAdditionalProperties().entrySet().stream()
        .map(interfaceEntry -> getSpecEvolutionFor(interfaceEntry, catalogueId))
        .collect(Collectors.toList());
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

  private SpecEvolution getSpecEvolutionFor(Map.Entry<String, Interface> interfaceEntry, CatalogueId catalogueId) {
    var catalogueInterfaceEntry = interfaceEntry.getValue();
    var specEvolutionConfig = catalogueInterfaceEntry.getSpecEvolutionConfig();

    var specFileRepo = SpecFileRepositoryResolver.resolveSpecFileRepository(catalogueInterfaceEntry, catalogueId);
    var specFilePath = catalogueInterfaceEntry.getSpecFile().getFilePath();

    return this.getSpecEvolution(interfaceEntry.getKey(), specEvolutionConfig, specFileRepo, specFilePath);
  }
}
