package spectacular.backend.catalogues;

import spectacular.backend.api.model.Catalogue;
import spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError;
import spectacular.backend.cataloguemanifest.configurationitem.GetCatalogueManifestConfigurationItemResult;

public class GetCatalogueForUserResult extends GetCatalogueManifestConfigurationItemResult {
  private final Catalogue catalogueDetails;

  private GetCatalogueForUserResult(ConfigurationItemError getConfigurationItemError, Catalogue catalogueDetails) {
    super(getConfigurationItemError);
    this.catalogueDetails = catalogueDetails;
  }

  public static GetCatalogueForUserResult createErrorResult(ConfigurationItemError getConfigurationItemError) {
    return new GetCatalogueForUserResult(getConfigurationItemError, null);
  }

  public static GetCatalogueForUserResult createFoundResult(Catalogue catalogueDetails) {
    return new GetCatalogueForUserResult(null, catalogueDetails);
  }

  public Catalogue getCatalogueDetails() {
    return catalogueDetails;
  }
}
