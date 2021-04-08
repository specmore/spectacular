package spectacular.backend.cataloguemanifest.interfaceentry;

import static spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError.createConfigError;
import static spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError.createNotFoundError;

import org.springframework.stereotype.Service;
import spectacular.backend.cataloguemanifest.catalogueentry.CatalogueEntryConfigurationResolver;
import spectacular.backend.common.CatalogueId;

@Service
public class CatalogueInterfaceEntryConfigurationResolver {
  private final CatalogueEntryConfigurationResolver catalogueEntryConfigurationResolver;

  public CatalogueInterfaceEntryConfigurationResolver(
      CatalogueEntryConfigurationResolver catalogueEntryConfigurationResolver) {
    this.catalogueEntryConfigurationResolver = catalogueEntryConfigurationResolver;
  }

  /**
   * Gets a catalogue manifest file and attempts to find and parse an interface entry in it for a given user.
   *
   * @param catalogueId an object containing the manifest file location and name of the catalogue entry the interface entry is inside
   * @param interfaceName the name of the interface entry
   * @param username the user that is trying access the catalogue
   * @return a GetInterfaceEntryConfigurationResult object with
   *     1. a successfully found and parsed interface entry
   *     2. an error if the interface entry could not be found or it was not valid
   */
  public GetInterfaceEntryConfigurationResult getCatalogueInterfaceEntryConfiguration(CatalogueId catalogueId,
                                                                                      String interfaceName,
                                                                                      String username) {
    var getCatalogueEntryConfigurationResult = catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, username);

    if (getCatalogueEntryConfigurationResult.hasError()) {
      return GetInterfaceEntryConfigurationResult.createErrorResult(getCatalogueEntryConfigurationResult.getError());
    }

    var catalogueEntry = getCatalogueEntryConfigurationResult.getCatalogueEntry();
    if (catalogueEntry.getInterfaces() == null || !catalogueEntry.getInterfaces().getAdditionalProperties().containsKey(interfaceName)) {
      return GetInterfaceEntryConfigurationResult.createErrorResult(
          createNotFoundError("Interface entry not found in Catalogue entry in manifest file: " + catalogueId.getCombined() +
              ", with name: " + interfaceName));
    }

    var catalogueInterfaceEntry = catalogueEntry.getInterfaces().getAdditionalProperties().get(interfaceName);

    if (catalogueInterfaceEntry == null || catalogueInterfaceEntry.getSpecFile() == null) {
      return GetInterfaceEntryConfigurationResult.createErrorResult(
          createConfigError("Interface entry in Catalogue entry in manifest file: " + catalogueId.getCombined() +
              ", with name: " + interfaceName + ", has no spec file location set."));
    }

    var manifestUri = getCatalogueEntryConfigurationResult.getManifestUri();
    return GetInterfaceEntryConfigurationResult.createSuccessfulResult(catalogueEntry, catalogueInterfaceEntry, manifestUri);
  }
}
