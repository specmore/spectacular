package spectacular.github.service.specs;

import java.util.List;

public class SpecLog {
    private final SpecItem latestAgreed;
    private final List<ProposedSpecChange> proposedChanges;

    public SpecLog(SpecItem latestAgreed, List<ProposedSpecChange> proposedChanges) {
        this.latestAgreed = latestAgreed;
        this.proposedChanges = proposedChanges;
    }

    public SpecItem getLatestAgreed() {
        return latestAgreed;
    }

    public List<ProposedSpecChange> getProposedChanges() {
        return proposedChanges;
    }
}
