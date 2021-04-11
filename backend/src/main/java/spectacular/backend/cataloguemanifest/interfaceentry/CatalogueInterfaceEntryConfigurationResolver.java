package spectacular.backend.cataloguemanifest.interfaceentry;

import static spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError.createConfigError;
import static spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError.createNotFoundError;

import org.springframework.stereotype.Service;
import spectacular.backend.cataloguemanifest.catalogueentry.GetCatalogueEntryConfigurationResult;
import spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError;
import spectacular.backend.cataloguemanifest.configurationitem.ResolveConfigurationItemResult;
import spectacular.backend.cataloguemanifest.model.Interface;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.common.RepositoryId;

@Service
public class CatalogueInterfaceEntryConfigurationResolver {

  public CatalogueInterfaceEntryConfigurationResolver() {
  }

  /**
   * Gets a catalogue manifest file and attempts to find and parse an interface entry in it for a given user.
   *
   * @param getCatalogueEntryConfigurationResult an object containing the catalogue entry the interface entry is inside
   * @param interfaceName the name of the interface entry
   * @return a GetInterfaceEntryConfigurationResult object with
   *     1. a successfully found and parsed interface entry
   *     2. an error if the interface entry could not be found or it was not valid
   */
  public GetInterfaceEntryConfigurationResult getCatalogueInterfaceEntryConfiguration(
      GetCatalogueEntryConfigurationResult getCatalogueEntryConfigurationResult,
      String interfaceName) {
    var catalogueId = getCatalogueEntryConfigurationResult.getCatalogueId();
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

    if (catalogueInterfaceEntry.getSpecFile().getRepo() == null) {
      catalogueInterfaceEntry.getSpecFile().setRepo(catalogueId.getRepositoryId().getNameWithOwner());
    }

    return GetInterfaceEntryConfigurationResult.createSuccessfulResult(catalogueInterfaceEntry, catalogueId, interfaceName);
  }

  public static class GetInterfaceEntryConfigurationResult extends ResolveConfigurationItemResult {
    private final Interface interfaceEntry;
    private final CatalogueId catalogueId;
    private final String interfaceName;

    private GetInterfaceEntryConfigurationResult(ConfigurationItemError error) {
      super(error);
      this.interfaceEntry = null;
      this.catalogueId = null;
      this.interfaceName = null;
    }

    private GetInterfaceEntryConfigurationResult(Interface interfaceEntry, CatalogueId catalogueId, String interfaceName) {
      super(null);
      this.interfaceEntry = interfaceEntry;
      this.catalogueId = catalogueId;
      this.interfaceName = interfaceName;
    }

    public Interface getInterfaceEntry() {
      return interfaceEntry;
    }

    private static GetInterfaceEntryConfigurationResult createErrorResult(ConfigurationItemError error) {
      return new GetInterfaceEntryConfigurationResult(error);
    }

    private static GetInterfaceEntryConfigurationResult createSuccessfulResult(Interface interfaceEntry,
                                                                              CatalogueId catalogueId,
                                                                              String interfaceName) {
      return new GetInterfaceEntryConfigurationResult(interfaceEntry, catalogueId, interfaceName);
    }

    public CatalogueId getCatalogueId() {
      return catalogueId;
    }

    public String getInterfaceName() {
      return interfaceName;
    }
  }
}
