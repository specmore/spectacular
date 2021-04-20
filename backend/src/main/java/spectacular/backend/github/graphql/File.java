package spectacular.backend.github.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;

public class File {
  private final String name;
  private final Blob object;

  public File(@JsonProperty("name") String name, @JsonProperty("object") Blob object) {
    this.name = name;
    this.object = object;
  }

  public String getName() {
    return name;
  }

  public Blob getObject() {
    return object;
  }
}
