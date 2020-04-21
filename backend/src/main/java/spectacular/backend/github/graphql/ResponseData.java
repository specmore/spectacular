package spectacular.backend.github.graphql;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseData {
  private final RepositoryWithPullRequests repository;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ResponseData(@JsonProperty("repository") RepositoryWithPullRequests repository) {
    this.repository = repository;
  }

  public RepositoryWithPullRequests getRepository() {
    return repository;
  }
}