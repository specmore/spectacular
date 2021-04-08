package spectacular.backend.interfaces;

import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import spectacular.backend.api.model.GetInterfaceResult;
import spectacular.backend.cataloguemanifest.SpecFileRepositoryResolver;
import spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError;
import spectacular.backend.cataloguemanifest.model.Interface;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.specevolution.SpecEvolutionService;
import spectacular.backend.specevolution.SpecEvolutionSummaryMapper;

@Service
public class InterfaceService {
  private static final Logger logger = LoggerFactory.getLogger(InterfaceService.class);

  private final RestApiClient restApiClient;
  private final SpecEvolutionService specEvolutionService;
  private final SpecEvolutionSummaryMapper specEvolutionSummaryMapper;

  /**
   * A service for returning information about an interface and its specification file.
   * @param restApiClient the rest client for retrieving information from the git service
   * @param specEvolutionService a helper service for building the evolutionary view of a specification file
   * @param specEvolutionSummaryMapper a helper mapper for creating summarised evolutionary views
   */
  public InterfaceService(RestApiClient restApiClient,
                          SpecEvolutionService specEvolutionService,
                          SpecEvolutionSummaryMapper specEvolutionSummaryMapper) {
    this.restApiClient = restApiClient;
    this.specEvolutionService = specEvolutionService;
    this.specEvolutionSummaryMapper = specEvolutionSummaryMapper;
  }

  /**
   * Get the contents of a spec file at the location described by a specific interface entry in a catalogue manifest.
   *
   * @param catalogueId the catalogue the interface belongs to
   * @param interfaceConfig the catalogue manifest interface entry to get the interface details for
   * @param ref the name of the git ref at which to get the file contents
   * @return a GetInterfaceFileContentsResult
   *     result with the InterfaceFileContents if the file is found and the user has access to it.
   *     Else it returns with an error.
   * @throws UnsupportedEncodingException if an error occurred while decoding the content
   */
  public GetInterfaceFileContentsResult getInterfaceFileContents(CatalogueId catalogueId, Interface interfaceConfig, String ref)
      throws UnsupportedEncodingException {
    if (interfaceConfig.getSpecFile() == null) {
      var errorMessage = "The requested interface spec file has no location configured.";
      var configError = ConfigurationItemError.createConfigError(errorMessage);
      return GetInterfaceFileContentsResult.createErrorResult(configError);
    }

    var fileRepo = SpecFileRepositoryResolver.resolveSpecFileRepository(interfaceConfig, catalogueId);
    var filePath = interfaceConfig.getSpecFile().getFilePath();

    try {
      var fileContentItem = restApiClient.getRepositoryContent(fileRepo, filePath, ref);
      var fileContents = fileContentItem.getDecodedContent();

      var interfaceFileContents = new InterfaceFileContents(fileContents, filePath);
      return GetInterfaceFileContentsResult.createFoundResult(interfaceFileContents);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        var errorMessage = "Interface specification file not found for path '" + filePath + "' in repo '" + fileRepo.getNameWithOwner() +
            "' at ref '" + ref + "'";
        logger.debug("A request for a interface spec file contents that does not exist was received. " + errorMessage);
        var notFoundError = ConfigurationItemError.createNotFoundError(errorMessage);
        return GetInterfaceFileContentsResult.createErrorResult(notFoundError);
      } else {
        throw e;
      }
    }
  }

  /**
   * Gets the details of an interface.
   *
   * @param catalogueId the catalogue the interface belongs to
   * @param interfaceConfig the catalogue manifest interface entry to get the interface details for
   * @param interfaceName the name of the manifest interface entry
   */
  public GetInterfaceResult getInterfaceDetails(CatalogueId catalogueId, Interface interfaceConfig, String interfaceName) {
    var specEvolutionConfig = interfaceConfig.getSpecEvolutionConfig();

    var specFileRepo = SpecFileRepositoryResolver.resolveSpecFileRepository(interfaceConfig, catalogueId);
    var specFilePath = interfaceConfig.getSpecFile().getFilePath();

    var specEvolution = specEvolutionService.getSpecEvolution(interfaceName, specEvolutionConfig, specFileRepo, specFilePath);
    var specEvolutionSummary = specEvolutionSummaryMapper.mapSpecEvolutionToSummary(specEvolution);

    return new GetInterfaceResult()
        .specEvolutionSummary(specEvolutionSummary)
        .specEvolution(specEvolution);
  }
}
