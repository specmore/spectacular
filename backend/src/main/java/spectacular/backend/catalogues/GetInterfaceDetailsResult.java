package spectacular.backend.catalogues;

import spectacular.backend.api.model.GetInterfaceResult;
import spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError;
import spectacular.backend.cataloguemanifest.configurationitem.ResolveConfigurationItemResult;

public class GetInterfaceDetailsResult extends ResolveConfigurationItemResult {
  private final GetInterfaceResult getInterfaceResult;

  private GetInterfaceDetailsResult(ConfigurationItemError getConfigurationItemError,
                                    GetInterfaceResult getInterfaceResult) {
    super(getConfigurationItemError);
    this.getInterfaceResult = getInterfaceResult;
  }

  public static GetInterfaceDetailsResult createErrorResult(ConfigurationItemError getConfigurationItemError) {
    return new GetInterfaceDetailsResult(getConfigurationItemError, null);
  }

  public static GetInterfaceDetailsResult createFoundResult(GetInterfaceResult getInterfaceResult) {
    return new GetInterfaceDetailsResult(null, getInterfaceResult);
  }

  public GetInterfaceResult getGetInterfaceResult() {
    return getInterfaceResult;
  }
}
