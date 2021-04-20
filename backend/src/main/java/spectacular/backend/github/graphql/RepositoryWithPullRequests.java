package spectacular.backend.github.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;

public class RepositoryWithPullRequests extends Repository {
  private final Connection<PullRequest> pullRequests;
  private final Connection<RepositoryRef> refs;

  /**
   * A repository object that is the root of a graph query.
   * @param nameWithOwner the identifier of the repo
   * @param url the url to the repository
   * @param pullRequests any pull requests that are targeting branches on the repository
   * @param refs any refs that are part of the repository
   */
  public RepositoryWithPullRequests(@JsonProperty("nameWithOwner") String nameWithOwner,
                                    @JsonProperty("url") URI url,
                                    @JsonProperty("pullRequests") Connection<PullRequest> pullRequests,
                                    @JsonProperty("refs") Connection<RepositoryRef> refs) {
    super(nameWithOwner, url);
    this.pullRequests = pullRequests;
    this.refs = refs;
  }

  public Connection<PullRequest> getPullRequests() {
    return pullRequests;
  }

  public Connection<RepositoryRef> getRefs() {
    return refs;
  }
}
