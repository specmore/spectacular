package spectacular.backend.github.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RepositoryRef extends Ref {
  private final String name;
  private final Commit target;
  private final Connection<PullRequest> associatedPullRequests;

  /**
   * A git ref object that is associated to a repository.
   * @param name the name of the ref
   * @param repository the repository it is associated to
   * @param target the git object it is referencing
   * @param associatedPullRequests any pull requests that target this ref it is a branch
   */
  public RepositoryRef(@JsonProperty("name") String name,
                       @JsonProperty("repository") Repository repository,
                       @JsonProperty("target") Commit target,
                       @JsonProperty("associatedPullRequests") Connection<PullRequest> associatedPullRequests
  ) {
    super(name, repository);
    this.name = name;
    this.target = target;
    this.associatedPullRequests = associatedPullRequests;
  }

  public String getName() {
    return name;
  }

  public Commit getTarget() {
    return target;
  }

  public Connection<PullRequest> getAssociatedPullRequests() {
    return associatedPullRequests;
  }
}
