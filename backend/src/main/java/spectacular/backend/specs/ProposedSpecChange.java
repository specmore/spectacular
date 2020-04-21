package spectacular.backend.specs;

import javax.validation.constraints.NotNull;
import spectacular.backend.pullrequests.PullRequest;

public class ProposedSpecChange {
  private final int id;
  private final PullRequest pullRequest;
  private final SpecItem specItem;

  public ProposedSpecChange(@NotNull PullRequest pullRequest, @NotNull SpecItem specItem) {
    this.id = pullRequest.getNumber();
    this.pullRequest = pullRequest;
    this.specItem = specItem;
  }

  public PullRequest getPullRequest() {
    return pullRequest;
  }

  public SpecItem getSpecItem() {
    return specItem;
  }

  public int getId() {
    return id;
  }
}
