package spectacular.backend.catalogues;

import spectacular.backend.api.model.Catalogue;
import spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemError;

public class GetCatalogueForUserResult {
  private final GetCatalogueManifestConfigurationItemError getConfigurationItemError;
  private final Catalogue catalogueDetails;

  private GetCatalogueForUserResult(GetCatalogueManifestConfigurationItemError getConfigurationItemError, Catalogue catalogueDetails) {
    this.getConfigurationItemError = getConfigurationItemError;
    this.catalogueDetails = catalogueDetails;
  }

  public static GetCatalogueForUserResult createErrorResult(GetCatalogueManifestConfigurationItemError getConfigurationItemError) {
    return new GetCatalogueForUserResult(getConfigurationItemError, null);
  }

  public static GetCatalogueForUserResult createFoundResult(Catalogue catalogueDetails) {
    return new GetCatalogueForUserResult(null, catalogueDetails);
  }

  public Catalogue getCatalogueDetails() {
    return catalogueDetails;
  }

  public GetCatalogueManifestConfigurationItemError getGetConfigurationItemError() {
    return getConfigurationItemError;
  }
}
