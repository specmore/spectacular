package spectacular.backend.cataloguemanifest.interfaceentry;

import java.net.URI;
import spectacular.backend.cataloguemanifest.catalogueentry.GetCatalogueEntryConfigurationResult;
import spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError;
import spectacular.backend.cataloguemanifest.model.Catalogue;
import spectacular.backend.cataloguemanifest.model.Interface;

public class GetInterfaceEntryConfigurationResult extends GetCatalogueEntryConfigurationResult {
  private final Interface interfaceEntry;

  protected GetInterfaceEntryConfigurationResult(ConfigurationItemError error) {
    super(error);
    this.interfaceEntry = null;
  }

  protected GetInterfaceEntryConfigurationResult(Catalogue catalogueEntry, Interface interfaceEntry, URI manifestUri) {
    super(catalogueEntry, manifestUri);
    this.interfaceEntry = interfaceEntry;
  }

  public Interface getInterfaceEntry() {
    return interfaceEntry;
  }

  public static GetInterfaceEntryConfigurationResult createErrorResult(ConfigurationItemError error) {
    return new GetInterfaceEntryConfigurationResult(error);
  }

  public static GetInterfaceEntryConfigurationResult createSuccessfulResult(Catalogue catalogueEntry,
                                                                            Interface interfaceEntry,
                                                                            URI manifestUri) {
    return new GetInterfaceEntryConfigurationResult(catalogueEntry, interfaceEntry, manifestUri);
  }
}
