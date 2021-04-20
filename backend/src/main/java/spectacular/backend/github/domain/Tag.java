package spectacular.backend.github.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tag {
  private final String name;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public Tag(@JsonProperty("name") String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
