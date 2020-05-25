package spectacular.backend.github.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;

public class RepositoryWithPullRequests extends Repository {
  private final Connection<PullRequest> pullRequests;

  public RepositoryWithPullRequests(@JsonProperty("nameWithOwner") String nameWithOwner,
                                    @JsonProperty("url") URI url, @JsonProperty("pullRequests")
                                        Connection<PullRequest> pullRequests) {
    super(nameWithOwner, url);
    this.pullRequests = pullRequests;
  }

  public Connection<PullRequest> getPullRequests() {
    return pullRequests;
  }
}
