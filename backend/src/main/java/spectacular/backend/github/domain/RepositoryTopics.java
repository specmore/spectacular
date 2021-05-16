package spectacular.backend.github.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RepositoryTopics {
  private final List<String> names;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public RepositoryTopics(@JsonProperty("names") List<String> names) {
    this.names = names;
  }

  public List<String> getNames() {
    return names;
  }
}
