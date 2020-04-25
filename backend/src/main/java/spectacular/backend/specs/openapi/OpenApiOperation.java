package spectacular.backend.specs.openapi;

public class OpenApiOperation {
  private final String path;
  private final String name;
  private final String topicName;

  /**
   * Constructs an OpenApiOperation representation of an operation declared in an OpenAPI spec.
   *
   * @param path the URL path of the operation
   * @param name the name of the operation
   * @param topicName any topic name declared in the extended properties
   */
  public OpenApiOperation(String path, String name, String topicName) {
    this.path = path;
    this.name = name;
    this.topicName = topicName;
  }

  public String getPath() {
    return path;
  }

  public String getName() {
    return name;
  }

  public String getTopicName() {
    return topicName;
  }
}
