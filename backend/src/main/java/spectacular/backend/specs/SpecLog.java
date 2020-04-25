package spectacular.backend.specs;

import java.util.List;
import javax.validation.constraints.NotNull;

public class SpecLog {
  private final String id;
  private final SpecItem latestAgreed;
  private final List<ProposedSpecChange> proposedChanges;

  /**
   * Constructs a SpecLog object that represents the current state of a spec item's evolution.
   *
   * @param latestAgreed the current agreed version of the SpecItem
   * @param proposedChanges a list of the open ProposedSpecChange objects
   */
  public SpecLog(@NotNull SpecItem latestAgreed, @NotNull List<ProposedSpecChange> proposedChanges) {
    this.id = latestAgreed.getRepository().getNameWithOwner() + "/" + latestAgreed.getFilePath();
    this.latestAgreed = latestAgreed;
    this.proposedChanges = proposedChanges;
  }

  public String getId() {
    return id;
  }

  public SpecItem getLatestAgreed() {
    return latestAgreed;
  }

  public List<ProposedSpecChange> getProposedChanges() {
    return proposedChanges;
  }
}
