package spectacular.github.service.specs;

import java.util.List;

public class SpecLog {
    private final SpecItem latestAgreed;
    private final List<SpecItem> proposedChanges;

    public SpecLog(SpecItem latestAgreed, List<SpecItem> proposedChanges) {
        this.latestAgreed = latestAgreed;
        this.proposedChanges = proposedChanges;
    }

    public SpecItem getLatestAgreed() {
        return latestAgreed;
    }

    public List<SpecItem> getProposedChanges() {
        return proposedChanges;
    }
}
