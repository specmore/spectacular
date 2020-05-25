package spectacular.backend.github.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;

public class Repository {
  private final String nameWithOwner;
  private final URI url;

  public Repository(@JsonProperty("nameWithOwner") String nameWithOwner,
                    @JsonProperty("url") URI url) {
    this.nameWithOwner = nameWithOwner;
    this.url = url;
  }

  public URI getUrl() {
    return url;
  }

  public String getNameWithOwner() {
    return nameWithOwner;
  }
}
