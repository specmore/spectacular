package spectacular.backend.catalogues;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import spectacular.backend.api.model.GetInterfaceResult;
import spectacular.backend.cataloguemanifest.catalogueentry.CatalogueEntryConfigurationResolver;
import spectacular.backend.cataloguemanifest.interfaceentry.CatalogueInterfaceEntryConfigurationResolver;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.interfaces.GetInterfaceFileContentsResult;
import spectacular.backend.interfaces.InterfaceService;

@Service
public class CatalogueService {
  private static final Logger logger = LoggerFactory.getLogger(CatalogueService.class);

  private final CatalogueEntryConfigurationResolver catalogueEntryConfigurationResolver;
  private final CatalogueInterfaceEntryConfigurationResolver catalogueInterfaceEntryConfigurationResolver;
  private final CatalogueMapper catalogueMapper;
  private final InterfaceService interfaceService;

  /**
   * A service component that encapsulates all the logic required to build Catalogue objects from the information stored in git repositories
   * accessible for a given request's installation context.
   * @param catalogueEntryConfigurationResolver a helper service for retrieving and parsing catalogue entries in manifest files
   * @param catalogueInterfaceEntryConfigurationResolver a helper service for retrieving and parsing interface entries in manifest files
   * @param catalogueMapper a helper service for mapping catalogue manifest objects to API model objects
   * @param interfaceService a service for retrieving more interface information for an interface configured in a catalogue manifest
   */
  public CatalogueService(CatalogueEntryConfigurationResolver catalogueEntryConfigurationResolver,
                          CatalogueInterfaceEntryConfigurationResolver catalogueInterfaceEntryConfigurationResolver,
                          CatalogueMapper catalogueMapper,
                          InterfaceService interfaceService) {
    this.catalogueEntryConfigurationResolver = catalogueEntryConfigurationResolver;
    this.catalogueInterfaceEntryConfigurationResolver = catalogueInterfaceEntryConfigurationResolver;
    this.catalogueMapper = catalogueMapper;
    this.interfaceService = interfaceService;
  }

  /**
   * Find all catalogues a given user can access in a specific organisation.
   *
   * @param orgName the name of the organisation
   * @param username the username of the user
   * @return a list of all the accessible catalogues
   */
  public List<spectacular.backend.api.model.Catalogue> findCataloguesForOrgAndUser(String orgName, String username) {
    var catalogueEntryResults = catalogueEntryConfigurationResolver.findCataloguesForOrgAndUser(orgName, username);

    return catalogueEntryResults.stream()
        .map(catalogueMapper::mapCatalogue)
        .collect(Collectors.toList());
  }

  /**
   * Get a Catalogue matching the given identifier and accessible for the given user.
   *
   * @param catalogueId the identifier giving the exact location of the catalogue definition
   * @param username the username of the user
   * @return A Catalogue object
   */
  public GetCatalogueForUserResult getCatalogueForUser(CatalogueId catalogueId, String username) {
    var getCatalogueEntryConfigurationResult = catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, username);

    if (getCatalogueEntryConfigurationResult.hasError()) {
      return GetCatalogueForUserResult.createErrorResult(getCatalogueEntryConfigurationResult.getError());
    }

    var catalogueDetails = catalogueMapper.mapCatalogue(getCatalogueEntryConfigurationResult);
    var catalogueEntry = getCatalogueEntryConfigurationResult.getCatalogueEntry();

    if (catalogueEntry.getInterfaces() != null) {
      var resolvedInterfaceEntries = catalogueEntry.getInterfaces().getAdditionalProperties().keySet().stream()
          .map(interfaceEntryName -> catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(
              getCatalogueEntryConfigurationResult, interfaceEntryName))
          .filter(getInterfaceEntryConfigurationResult -> !getInterfaceEntryConfigurationResult.hasError())
          .collect(Collectors.toList());;

      var specEvolutionSummaries = resolvedInterfaceEntries.stream()
          .map(this.interfaceService::getInterfaceDetails)
          .map(GetInterfaceResult::getSpecEvolutionSummary)
          .collect(Collectors.toList());

      catalogueDetails = catalogueDetails.specEvolutionSummaries(specEvolutionSummaries);
    }

    return GetCatalogueForUserResult.createFoundResult(catalogueDetails);
  }

  /**
   * Get details about an interface that is listed in a catalogue for a given user.
   * @param catalogueId an identifier object containing the exact location of the catalogue definition
   * @param interfaceName the name of the interface entry in the catalogue definition
   * @param username the username of the user trying to access the interface details
   * @return a GetInterfaceDetailsResult object that indicates several outcomes:
   *     1. The interface entry could not be found due to the reason indicated.
   *     2. The interface entry could not be retrieved due to a configuration problem in the catalogue manifest file.
   *     3. The interface details.
   */
  public GetInterfaceDetailsResult getInterfaceDetails(CatalogueId catalogueId, String interfaceName, String username) {
    var getCatalogueEntryConfigurationResult = catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, username);

    if (getCatalogueEntryConfigurationResult.hasError()) {
      return GetInterfaceDetailsResult.createErrorResult(getCatalogueEntryConfigurationResult.getError());
    }

    var getInterfaceEntryConfigurationResult = catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(
        getCatalogueEntryConfigurationResult, interfaceName);

    if (getInterfaceEntryConfigurationResult.hasError()) {
      return GetInterfaceDetailsResult.createErrorResult(getInterfaceEntryConfigurationResult.getError());
    }

    var interfaceDetailsResult = this.interfaceService.getInterfaceDetails(getInterfaceEntryConfigurationResult);

    var catalogueDetails = catalogueMapper.mapCatalogue(getCatalogueEntryConfigurationResult);
    var interfaceDetails = interfaceDetailsResult.catalogue(catalogueDetails);

    return GetInterfaceDetailsResult.createFoundResult(interfaceDetails);
  }

  /**
   * Get contents of an interface specification file that is listed in a catalogue for a given user.
   * @param catalogueId an identifier object containing the exact location of the catalogue definition
   * @param interfaceName the name of the interface entry in the catalogue definition
   * @param ref a specific point in the version control history of the file to get the contents at
   * @param username the username of the user trying to access the interface details
   * @return a InterfaceFileContents containing the file contents and other file information
   * @throws UnsupportedEncodingException if the contents of the file can't be decoded
   */
  public GetInterfaceFileContentsResult getInterfaceFileContents(CatalogueId catalogueId, String interfaceName, String ref, String username)
      throws UnsupportedEncodingException {
    var getCatalogueEntryConfigurationResult = catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, username);

    if (getCatalogueEntryConfigurationResult.hasError()) {
      return GetInterfaceFileContentsResult.createErrorResult(getCatalogueEntryConfigurationResult.getError());
    }

    var getInterfaceEntryConfigurationResult = catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(
        getCatalogueEntryConfigurationResult, interfaceName);

    if (getInterfaceEntryConfigurationResult.hasError()) {
      return GetInterfaceFileContentsResult.createErrorResult(getInterfaceEntryConfigurationResult.getError());
    }

    var catalogueInterfaceEntry = getInterfaceEntryConfigurationResult.getInterfaceEntry();

    return this.interfaceService.getInterfaceFileContents(catalogueId, catalogueInterfaceEntry, ref);
  }
}
