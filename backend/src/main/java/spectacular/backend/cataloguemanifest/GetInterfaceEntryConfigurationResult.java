package spectacular.backend.cataloguemanifest;

import spectacular.backend.cataloguemanifest.model.Catalogue;
import spectacular.backend.cataloguemanifest.model.Interface;

public class GetInterfaceEntryConfigurationResult extends GetCatalogueEntryConfigurationResult {
  private final Interface interfaceEntry;

  protected GetInterfaceEntryConfigurationResult(GetCatalogueManifestConfigurationItemError error) {
    super(error);
    this.interfaceEntry = null;
  }

  protected GetInterfaceEntryConfigurationResult(Catalogue catalogueEntry, Interface interfaceEntry) {
    super(catalogueEntry);
    this.interfaceEntry = interfaceEntry;
  }

  public Interface getInterfaceEntry() {
    return interfaceEntry;
  }

  public static GetInterfaceEntryConfigurationResult createErrorResult(GetCatalogueManifestConfigurationItemError error) {
    return new GetInterfaceEntryConfigurationResult(error);
  }

  public static GetInterfaceEntryConfigurationResult createSuccessfulResult(Catalogue catalogueEntry, Interface interfaceEntry) {
    return new GetInterfaceEntryConfigurationResult(catalogueEntry, interfaceEntry);
  }
}
