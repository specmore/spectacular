package spectacular.github.service.specs;

import spectacular.github.service.pullrequests.PullRequest;

public class ProposedSpecChange {
    private final PullRequest pullRequest;
    private final SpecItem specItem;

    public ProposedSpecChange(PullRequest pullRequest, SpecItem specItem) {
        this.pullRequest = pullRequest;
        this.specItem = specItem;
    }

    public PullRequest getPullRequest() {
        return pullRequest;
    }

    public SpecItem getSpecItem() {
        return specItem;
    }
}
