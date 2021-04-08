package spectacular.backend.interfaces;

import spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError;
import spectacular.backend.cataloguemanifest.configurationitem.GetCatalogueManifestConfigurationItemResult;

public class GetInterfaceFileContentsResult extends GetCatalogueManifestConfigurationItemResult {
  private final InterfaceFileContents interfaceFileContents;

  protected GetInterfaceFileContentsResult(ConfigurationItemError error, InterfaceFileContents interfaceFileContents) {
    super(error);
    this.interfaceFileContents = interfaceFileContents;
  }

  public static GetInterfaceFileContentsResult createErrorResult(ConfigurationItemError getConfigurationItemError) {
    return new GetInterfaceFileContentsResult(getConfigurationItemError, null);
  }

  public static GetInterfaceFileContentsResult createFoundResult(InterfaceFileContents interfaceFileContents) {
    return new GetInterfaceFileContentsResult(null, interfaceFileContents);
  }

  public InterfaceFileContents getInterfaceFileContents() {
    return interfaceFileContents;
  }
}
