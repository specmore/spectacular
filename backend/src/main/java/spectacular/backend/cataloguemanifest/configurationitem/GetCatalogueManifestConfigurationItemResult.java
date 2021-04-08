package spectacular.backend.cataloguemanifest.configurationitem;

public abstract class GetCatalogueManifestConfigurationItemResult {
  private final ConfigurationItemError error;

  protected GetCatalogueManifestConfigurationItemResult(ConfigurationItemError error) {
    this.error = error;
  }

  public boolean hasError() {
    return error != null;
  }

  public ConfigurationItemError getError() {
    return error;
  }
}
