package spectacular.backend.catalogues;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import spectacular.backend.api.model.Catalogue;
import spectacular.backend.cataloguemanifest.CatalogueManifestParser;
import spectacular.backend.cataloguemanifest.CatalogueManifestProvider;
import spectacular.backend.cataloguemanifest.GetAndParseCatalogueResult;
import spectacular.backend.cataloguemanifest.GetCatalogueManifestFileContentResult;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.interfaces.InterfaceFileContents;
import spectacular.backend.interfaces.InterfaceService;

@Service
public class CatalogueService {
  private static final Logger logger = LoggerFactory.getLogger(CatalogueService.class);

  private final CatalogueManifestParser catalogueManifestParser;
  private final CatalogueManifestProvider catalogueManifestProvider;
  private final CatalogueMapper catalogueMapper;
  private final InterfaceService interfaceService;

  /**
   * A service component that encapsulates all the logic required to build Catalogue objects from the information stored in git repositories
   * accessible for a given request's installation context.
   * @param catalogueManifestParser a helper service to parse catalogue manifest file content into concrete objects
   * @param catalogueManifestProvider a data provider that retrieves catalogue manifest files from the data source
   * @param catalogueMapper a helper service for mapping catalogue manifest objects to API model objects
   * @param interfaceService a service for retrieving more interface information for an interface configured in a catalogue manifest
   */
  public CatalogueService(CatalogueManifestParser catalogueManifestParser,
                          CatalogueManifestProvider catalogueManifestProvider,
                          CatalogueMapper catalogueMapper,
                          InterfaceService interfaceService) {
    this.catalogueManifestParser = catalogueManifestParser;
    this.catalogueManifestProvider = catalogueManifestProvider;
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
    var manifestFiles = this.catalogueManifestProvider.findCatalogueManifestsForOrg(orgName, username);

    return manifestFiles.stream()
        .map(this::getCataloguesFromManifest)
        .flatMap(Collection::stream)
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
    GetAndParseCatalogueResult getAndParseCatalogueResult = catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, username);

    if (!getAndParseCatalogueResult.isCatalogueManifestFileExists()) {
      return GetCatalogueForUserResult.createNotFoundResult("Catalogue manifest file not found: " + catalogueId.getFullPath());
    }

    var catalogueEntryParseResult = getAndParseCatalogueResult.getCatalogueParseResult();

    if (catalogueEntryParseResult.isCatalogueEntryNotFound()) {
      return GetCatalogueForUserResult.createNotFoundResult("Catalogue entry in manifest file not found: " + catalogueId.getCombined());
    }

    if (catalogueEntryParseResult.isCatalogueEntryContainsError()) {
      var catalogueDetails = catalogueMapper.createForParseError(catalogueEntryParseResult.getError(), catalogueId);
      return GetCatalogueForUserResult.createFoundResult(catalogueDetails);
    }

    var catalogueEntry = catalogueEntryParseResult.getCatalogue();
    var manifestUrl = getAndParseCatalogueResult.getCatalogueManifestFileHtmlUrl();
    var catalogueDetails = catalogueMapper.mapCatalogue(catalogueEntry, catalogueId, manifestUrl);

    var specEvolutionSummaries = catalogueEntry.getInterfaces().getAdditionalProperties().entrySet().stream()
        .map(interfaceEntry -> {
          var interfaceDetails = this.interfaceService.getInterfaceDetails(catalogueId,interfaceEntry.getValue(), interfaceEntry.getKey());
          return interfaceDetails.getSpecEvolutionSummary();
        })
        .collect(Collectors.toList());

    var catalogueDetailsWithInterfaceSummaries = catalogueDetails.specEvolutionSummaries(specEvolutionSummaries);

    return GetCatalogueForUserResult.createFoundResult(catalogueDetailsWithInterfaceSummaries);
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
    GetAndParseCatalogueResult getAndParseCatalogueResult = catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, username);

    if (!getAndParseCatalogueResult.isCatalogueManifestFileExists()) {
      return GetInterfaceDetailsResult.createNotFoundResult("Catalogue manifest file not found: " + catalogueId.getFullPath());
    }

    var catalogueEntryParseResult = getAndParseCatalogueResult.getCatalogueParseResult();

    if (catalogueEntryParseResult.isCatalogueEntryNotFound()) {
      return GetInterfaceDetailsResult.createNotFoundResult("Catalogue entry in manifest file not found: " + catalogueId.getCombined());
    }

    if (catalogueEntryParseResult.isCatalogueEntryContainsError()) {
      return GetInterfaceDetailsResult.createConfigErrorResult("Catalogue entry in manifest file: " + catalogueId.getCombined() +
          ", has parse error: " + catalogueEntryParseResult.getError());
    }

    var catalogueEntry = catalogueEntryParseResult.getCatalogue();
    if (!catalogueEntry.getInterfaces().getAdditionalProperties().containsKey(interfaceName)) {
      return GetInterfaceDetailsResult.createNotFoundResult("Interface entry not found in Catalogue entry in manifest file: " +
          catalogueId.getCombined() + ", with name: " + interfaceName);
    }

    var catalogueInterfaceEntry = catalogueEntry.getInterfaces().getAdditionalProperties().get(interfaceName);
    var interfaceDetailsResult = this.interfaceService.getInterfaceDetails(catalogueId, catalogueInterfaceEntry, interfaceName);

    if (interfaceDetailsResult == null) {
      return GetInterfaceDetailsResult.createConfigErrorResult("Interface entry in Catalogue entry in manifest file: " +
          catalogueId.getCombined() + ", with name: " + interfaceName + ", has no spec file location set.");
    }

    var manifestUrl = getAndParseCatalogueResult.getCatalogueManifestFileHtmlUrl();
    var catalogueDetails = catalogueMapper.mapCatalogue(catalogueEntry, catalogueId, manifestUrl);
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
  public InterfaceFileContents getInterfaceFileContents(CatalogueId catalogueId, String interfaceName, String ref, String username)
      throws UnsupportedEncodingException {
    GetAndParseCatalogueResult getAndParseCatalogueResult = catalogueManifestProvider.getAndParseCatalogueInManifest(catalogueId, username);

    if (!getAndParseCatalogueResult.isCatalogueManifestFileExists()) {
      return null;
    }

    var parseError = getAndParseCatalogueResult.getCatalogueParseResult().getError();
    if (parseError != null) {
      logger.warn("A request for an interface entry in a catalogue manifest that does not parse correctly was received. " +
          "CatalogueId: {} and ParseError '{}'", catalogueId, parseError);
      throw new RuntimeException("An error occurred while parsing the catalogue manifest file for interface requested.");
    }

    var catalogue = getAndParseCatalogueResult.getCatalogueParseResult().getCatalogue();

    if (catalogue == null) {
      return null;
    }

    var catalogueInterfaceEntry = catalogue.getInterfaces().getAdditionalProperties().get(interfaceName);

    if (catalogueInterfaceEntry == null) {
      return null;
    }

    return this.interfaceService.getInterfaceFileContents(catalogueId, catalogueInterfaceEntry, ref);
  }

  private List<Catalogue> getCataloguesFromManifest(GetCatalogueManifestFileContentResult getCatalogueManifestFileContentResult) {
    var manifestId = getCatalogueManifestFileContentResult.getCatalogueManifestId();

    if (getCatalogueManifestFileContentResult.isFileNotFoundResult()) {
      logger.warn("A manifest file was found during a search but the actual file contents could not subsequently be found for: " +
          manifestId.getFullPath());
      return Collections.emptyList();
    }

    var fileContentItem = getCatalogueManifestFileContentResult.getCatalogueManifestContent();
    var parseResult = catalogueManifestParser.parseManifestFileContentItem(fileContentItem);

    if (parseResult.getError() != null) {
      return Collections.singletonList(catalogueMapper.createForParseError(parseResult.getError(), manifestId));
    }

    return catalogueMapper.mapCatalogueManifestEntries(parseResult.getCatalogueManifest(), manifestId, fileContentItem.getHtml_url());
  }
}
