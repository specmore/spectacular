package spectacular.backend.catalogues;

import spectacular.backend.api.model.GetInterfaceResult;
import spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemError;
import spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemResult;

public class GetInterfaceDetailsResult extends GetCatalogueManifestConfigurationItemResult {
  private final GetInterfaceResult getInterfaceResult;

  private GetInterfaceDetailsResult(GetCatalogueManifestConfigurationItemError getConfigurationItemError,
                                    GetInterfaceResult getInterfaceResult) {
    super(getConfigurationItemError);
    this.getInterfaceResult = getInterfaceResult;
  }

  public static GetInterfaceDetailsResult createErrorResult(GetCatalogueManifestConfigurationItemError getConfigurationItemError) {
    return new GetInterfaceDetailsResult(getConfigurationItemError, null);
  }

  public static GetInterfaceDetailsResult createFoundResult(GetInterfaceResult getInterfaceResult) {
    return new GetInterfaceDetailsResult(null, getInterfaceResult);
  }

  public GetInterfaceResult getGetInterfaceResult() {
    return getInterfaceResult;
  }
}
