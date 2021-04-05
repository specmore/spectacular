package spectacular.backend.cataloguemanifest;

public abstract class GetCatalogueManifestConfigurationItemResult {
  private final GetCatalogueManifestConfigurationItemError error;

  protected GetCatalogueManifestConfigurationItemResult(GetCatalogueManifestConfigurationItemError error) {
    this.error = error;
  }

  public boolean hasError() {
    return error != null;
  }

  public GetCatalogueManifestConfigurationItemError getError() {
    return error;
  }
}
