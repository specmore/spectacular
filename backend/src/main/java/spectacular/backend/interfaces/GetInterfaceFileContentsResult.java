package spectacular.backend.interfaces;

import spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemError;
import spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemResult;

public class GetInterfaceFileContentsResult extends GetCatalogueManifestConfigurationItemResult {
  private final InterfaceFileContents interfaceFileContents;

  protected GetInterfaceFileContentsResult(GetCatalogueManifestConfigurationItemError error, InterfaceFileContents interfaceFileContents) {
    super(error);
    this.interfaceFileContents = interfaceFileContents;
  }

  public static GetInterfaceFileContentsResult createErrorResult(GetCatalogueManifestConfigurationItemError getConfigurationItemError) {
    return new GetInterfaceFileContentsResult(getConfigurationItemError, null);
  }

  public static GetInterfaceFileContentsResult createFoundResult(InterfaceFileContents interfaceFileContents) {
    return new GetInterfaceFileContentsResult(null, interfaceFileContents);
  }

  public InterfaceFileContents getInterfaceFileContents() {
    return interfaceFileContents;
  }
}
