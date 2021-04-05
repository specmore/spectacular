package spectacular.backend.catalogues;

import spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemError;

public class GetCatalogueConfigurationItemResult {
  protected final GetCatalogueManifestConfigurationItemError getConfigurationItemError;

  public GetCatalogueConfigurationItemResult(
      GetCatalogueManifestConfigurationItemError getConfigurationItemError) {
    this.getConfigurationItemError = getConfigurationItemError;
  }

  public GetCatalogueManifestConfigurationItemError getGetConfigurationItemError() {
    return getConfigurationItemError;
  }
}
