package spectacular.backend.specs.openapi;

public class OpenApiOperation {
  private final String path;
  private final String name;
  private final String topicName;

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
