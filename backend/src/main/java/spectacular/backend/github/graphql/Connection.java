package spectacular.backend.github.graphql;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Connection<T> {
  private final int count;
  private final List<T> nodes;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public Connection(@JsonProperty("totalCount") int count, @JsonProperty("nodes") List<T> nodes) {
    this.count = count;
    this.nodes = nodes;
  }

  public List<T> getNodes() {
    return nodes;
  }

  public int getCount() {
    return count;
  }
}
