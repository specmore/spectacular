package spectacular.backend.specs.openapi;

import java.util.List;

public class OpenApiSpecParseResult {
  private final OpenApiSpec openApiSpec;
  private final List<String> errors;

  public OpenApiSpecParseResult(OpenApiSpec openApiSpec, List<String> errors) {
    this.openApiSpec = openApiSpec;
    this.errors = errors;
  }

  public OpenApiSpec getOpenApiSpec() {
    return openApiSpec;
  }

  public List<String> getErrors() {
    return errors;
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }
}
