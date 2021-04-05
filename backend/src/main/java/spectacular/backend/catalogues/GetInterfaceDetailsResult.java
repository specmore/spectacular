package spectacular.backend.catalogues;

import spectacular.backend.api.model.GetInterfaceResult;

public class GetInterfaceDetailsResult extends GetCatalogueManifestConfiguredResourceResult {
  private final GetInterfaceResult getInterfaceResult;

  private GetInterfaceDetailsResult(boolean isNotFound, boolean isConfigError, String notFoundErrorMessage, String configErrorMessage,
                                    GetInterfaceResult getInterfaceResult) {
    super(isNotFound, isConfigError, notFoundErrorMessage, configErrorMessage);
    this.getInterfaceResult = getInterfaceResult;
  }

  public static GetInterfaceDetailsResult createNotFoundResult(String error) {
    return new GetInterfaceDetailsResult(true, false, error, null, null);
  }

  public static GetInterfaceDetailsResult createConfigErrorResult(String error) {
    return new GetInterfaceDetailsResult(false, true, null, error, null);
  }

  public static GetInterfaceDetailsResult createFoundResult(GetInterfaceResult getInterfaceResult) {
    return new GetInterfaceDetailsResult(false, false, null, null, getInterfaceResult);
  }

  public GetInterfaceResult getGetInterfaceResult() {
    return getInterfaceResult;
  }

}
