package spectacular.backend.github.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Repository {
  private final String nameWithOwner;
  private final String url;

  public Repository(@JsonProperty("nameWithOwner") String nameWithOwner,
                    @JsonProperty("url") String url) {
    this.nameWithOwner = nameWithOwner;
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public String getNameWithOwner() {
    return nameWithOwner;
  }
}
