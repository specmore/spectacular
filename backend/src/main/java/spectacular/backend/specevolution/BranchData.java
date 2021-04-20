package spectacular.backend.specevolution;

import java.util.Collection;
import spectacular.backend.github.pullrequests.PullRequest;
import spectacular.backend.github.refs.BranchRef;

public class BranchData {
  private final BranchRef branch;
  private final Collection<PullRequest> associatedPullRequest;

  public BranchData(BranchRef branch, Collection<PullRequest> associatedPullRequest) {
    this.branch = branch;
    this.associatedPullRequest = associatedPullRequest;
  }

  public BranchRef getBranch() {
    return branch;
  }

  public Collection<PullRequest> getAssociatedPullRequest() {
    return associatedPullRequest;
  }
}
