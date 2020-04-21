package spectacular.backend.github.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RepositoryWithPullRequests extends Repository {
  private final Connection<PullRequest> pullRequests;

  public RepositoryWithPullRequests(@JsonProperty("nameWithOwner") String nameWithOwner,
                                    @JsonProperty("url") String url, @JsonProperty("pullRequests")
                                        Connection<PullRequest> pullRequests) {
    super(nameWithOwner, url);
    this.pullRequests = pullRequests;
  }

  public Connection<PullRequest> getPullRequests() {
    return pullRequests;
  }
}
