package spectacular.backend.specs;

import javax.validation.constraints.NotNull;
import java.util.List;

public class SpecLog {
    private final String id;
    private final SpecItem latestAgreed;
    private final List<ProposedSpecChange> proposedChanges;

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
