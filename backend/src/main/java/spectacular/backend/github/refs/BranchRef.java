package spectacular.backend.github.refs;

import java.util.List;
import java.util.stream.Collectors;
import spectacular.backend.github.graphql.RepositoryRef;
import spectacular.backend.github.pullrequests.PullRequest;

public class BranchRef {
  private final String name;
  private final List<PullRequest> associatedPullRequests;
  private final String specFileContents;

  /**
   * An object that represents a Branch in a Git Repository.
   * @param name the name of the branch
   * @param associatedPullRequests an PRs that are open against this branch
   * @param specFileContents the contents of the spec file in this branch
   */
  public BranchRef(String name, List<PullRequest> associatedPullRequests, String specFileContents) {
    this.name = name;
    this.associatedPullRequests = associatedPullRequests;
    this.specFileContents = specFileContents;
  }

  /**
   * A factory method for creating BranchRef objects from GraphQL data.
   * @param ref the graphql data
   * @return a newly constructed BranchRef object
   */
  public static BranchRef createBranchRefFrom(RepositoryRef ref) {
    var name = ref.getName();
    var associatedPullRequests = ref.getAssociatedPullRequests().getNodes().stream()
        .filter(pullRequest -> pullRequest.getHeadRef() != null)
        .map(PullRequest::createPullRequestFrom)
        .collect(Collectors.toList());
    var specFileContents = ref.getTarget().getFile().getObject().getText();
    return new BranchRef(name, associatedPullRequests, specFileContents);
  }

  public String getName() {
    return name;
  }

  public List<PullRequest> getAssociatedPullRequests() {
    return associatedPullRequests;
  }

  public String getSpecFileContents() {
    return specFileContents;
  }
}
