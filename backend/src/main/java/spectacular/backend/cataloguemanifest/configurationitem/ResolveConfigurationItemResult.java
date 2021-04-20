package spectacular.backend.cataloguemanifest.configurationitem;

public abstract class ResolveConfigurationItemResult {
  private final ConfigurationItemError error;

  protected ResolveConfigurationItemResult(ConfigurationItemError error) {
    this.error = error;
  }

  public boolean hasError() {
    return error != null;
  }

  public ConfigurationItemError getError() {
    return error;
  }
}
