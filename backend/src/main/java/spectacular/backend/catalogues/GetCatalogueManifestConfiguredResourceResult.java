package spectacular.backend.catalogues;

public abstract class GetCatalogueManifestConfiguredResourceResult {
  protected final boolean isNotFound;
  protected final boolean isConfigError;
  protected final String notFoundErrorMessage;
  protected final String configErrorMessage;

  protected GetCatalogueManifestConfiguredResourceResult(
      boolean isNotFound, boolean isConfigError, String notFoundErrorMessage, String configErrorMessage) {
    this.isNotFound = isNotFound;
    this.isConfigError = isConfigError;
    this.notFoundErrorMessage = notFoundErrorMessage;
    this.configErrorMessage = configErrorMessage;
  }

  public boolean isNotFound() {
    return isNotFound;
  }

  public String getNotFoundErrorMessage() {
    return notFoundErrorMessage;
  }

  public String getConfigErrorMessage() {
    return configErrorMessage;
  }

  public boolean isConfigError() {
    return isConfigError;
  }
}
