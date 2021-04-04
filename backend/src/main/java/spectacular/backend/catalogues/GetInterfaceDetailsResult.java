package spectacular.backend.catalogues;

import spectacular.backend.api.model.GetInterfaceResult;

public class GetInterfaceDetailsResult {
  private final boolean isNotFound;
  private final boolean isConfigError;
  private final String notFoundErrorMessage;
  private final String configErrorMessage;
  private final GetInterfaceResult getInterfaceResult;

  private GetInterfaceDetailsResult(boolean isNotFound, boolean isConfigError, String notFoundErrorMessage, String configErrorMessage,
                                    GetInterfaceResult getInterfaceResult) {
    this.isNotFound = isNotFound;
    this.isConfigError = isConfigError;
    this.notFoundErrorMessage = notFoundErrorMessage;
    this.configErrorMessage = configErrorMessage;
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

  public boolean isNotFound() {
    return isNotFound;
  }

  public String getNotFoundErrorMessage() {
    return notFoundErrorMessage;
  }

  public GetInterfaceResult getGetInterfaceResult() {
    return getInterfaceResult;
  }

  public String getConfigErrorMessage() {
    return configErrorMessage;
  }

  public boolean isConfigError() {
    return isConfigError;
  }
}
