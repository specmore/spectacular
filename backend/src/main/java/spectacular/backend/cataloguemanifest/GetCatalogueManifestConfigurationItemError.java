package spectacular.backend.cataloguemanifest;

import static spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemErrorType.CONFIG_ERROR;
import static spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemErrorType.NOT_FOUND;

public class GetCatalogueManifestConfigurationItemError {
  private final GetCatalogueManifestConfigurationItemErrorType type;
  private final String message;

  public GetCatalogueManifestConfigurationItemError(
      GetCatalogueManifestConfigurationItemErrorType type, String message) {
    this.type = type;
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public GetCatalogueManifestConfigurationItemErrorType getType() {
    return type;
  }

  public static GetCatalogueManifestConfigurationItemError createNotFoundError(String message) {
    return new GetCatalogueManifestConfigurationItemError(NOT_FOUND, message);
  }

  public static GetCatalogueManifestConfigurationItemError createConfigError(String message) {
    return new GetCatalogueManifestConfigurationItemError(CONFIG_ERROR, message);
  }
}
