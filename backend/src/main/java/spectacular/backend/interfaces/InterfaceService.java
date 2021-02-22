package spectacular.backend.interfaces;

import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import spectacular.backend.api.model.EvolutionBranch;
import spectacular.backend.api.model.SpecEvolution;
import spectacular.backend.catalogues.CatalogueService;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.specevolution.EvolutionBranchBuilder;

@Service
public class InterfaceService {
  private static final Logger logger = LoggerFactory.getLogger(InterfaceService.class);

  private final CatalogueService catalogueService;
  private final RestApiClient restApiClient;
  private final EvolutionBranchBuilder evolutionBranchBuilder;

  /**
   * A service for returning information about an interface and its specification file.
   * @param catalogueService the catalogue service used to get information about where the spec file is located
   * @param restApiClient the rest client for retrieving information from the git service
   * @param evolutionBranchBuilder a helper service for building the evolutionary view of a specification file
   */
  public InterfaceService(CatalogueService catalogueService, RestApiClient restApiClient, EvolutionBranchBuilder evolutionBranchBuilder) {
    this.catalogueService = catalogueService;
    this.restApiClient = restApiClient;
    this.evolutionBranchBuilder = evolutionBranchBuilder;
  }

  /**
   * Get the contents of a spec file at the location described by a specific interface entry in a catalogue manifest.
   *
   * @param catalogueId the location and name of the catalogue
   * @param interfaceName the name of the interface entry in the catalogue
   * @param ref the name of the git ref at which to get the file contents
   * @param username the name of the user requesting the file contents
   * @return a InterfaceFileContents result if the file is found and the user has access to it. Else it returns null
   * @throws UnsupportedEncodingException if an error occurred while decoding the content
   */
  public InterfaceFileContents getInterfaceFileContents(CatalogueId catalogueId, String interfaceName, String ref, String username)
      throws UnsupportedEncodingException {
    var catalogueInterfaceEntry = this.catalogueService.getInterfaceEntry(catalogueId, interfaceName,username);

    if (catalogueInterfaceEntry == null || catalogueInterfaceEntry.getSpecFile() == null) {
      return null;
    }

    RepositoryId fileRepo;
    if (catalogueInterfaceEntry.getSpecFile().getRepo() != null) {
      fileRepo = RepositoryId.createForNameWithOwner(catalogueInterfaceEntry.getSpecFile().getRepo());
    } else {
      fileRepo = catalogueId.getRepositoryId();
    }
    var filePath = catalogueInterfaceEntry.getSpecFile().getFilePath();

    try {
      var fileContentItem = restApiClient.getRepositoryContent(fileRepo, filePath, ref);
      var fileContents = fileContentItem.getDecodedContent();

      return new InterfaceFileContents(fileContents, filePath);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.debug("A request for a interface spec file that does not exist was received. Spec File Location: {}",
            catalogueInterfaceEntry.getSpecFile());
        return null;
      } else {
        throw e;
      }
    }
  }

  /**
   * Get an evolutionary view of the spec file for an interface.
   *
   * @param catalogueId the catalogue the interface belongs to
   * @param interfaceName the name of the interface
   * @param username the name of the user requesting the spec evolution
   */
  public SpecEvolution getSpecEvolution(CatalogueId catalogueId, String interfaceName, String username) {
    var catalogueInterfaceEntry = this.catalogueService.getInterfaceEntry(catalogueId, interfaceName,username);

    if (catalogueInterfaceEntry == null || catalogueInterfaceEntry.getSpecFile() == null) {
      return null;
    }

    RepositoryId fileRepo;
    if (catalogueInterfaceEntry.getSpecFile().getRepo() != null) {
      fileRepo = RepositoryId.createForNameWithOwner(catalogueInterfaceEntry.getSpecFile().getRepo());
    } else {
      fileRepo = catalogueId.getRepositoryId();
    }

    var tags = this.restApiClient.getRepositoryTags(fileRepo);

    var mainBranchName = "master";

    var mainBranchTagEvolutionItems = evolutionBranchBuilder.generateEvolutionItems(fileRepo, mainBranchName, tags);

    var mainBranch = new EvolutionBranch().branchName("master").evolutionItems(mainBranchTagEvolutionItems);

    var specEvolution = new SpecEvolution().main(mainBranch);

    return specEvolution;
  }
}
