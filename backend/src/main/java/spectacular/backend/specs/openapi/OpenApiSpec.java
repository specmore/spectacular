package spectacular.backend.specs.openapi;

import java.util.List;

public class OpenApiSpec {
  private final String title;
  private final String version;
  private final List<OpenApiOperation> operations;

  /**
   * Constructs an OpenApiSpec object capturing specific values from the contents of a OpenAPI YAML file.
   *
   * @param title the title of the API
   * @param version the version of the spec
   * @param operations the list of operations described for the interface
   */
  public OpenApiSpec(String title, String version, List<OpenApiOperation> operations) {
    this.title = title;
    this.version = version;
    this.operations = operations;
  }

  public String getTitle() {
    return title;
  }

  public String getVersion() {
    return version;
  }

  public List<OpenApiOperation> getOperations() {
    return operations;
  }
}
