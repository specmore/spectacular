package spectacular.backend.specs;

import org.springframework.stereotype.Service;
import spectacular.backend.common.Repository;
import spectacular.backend.pullrequests.PullRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecLogService {
    private final static String LATEST_AGREED_BRANCH = "master";

    private final SpecService specService;

    public SpecLogService(SpecService specService) {
        this.specService = specService;
    }

    public SpecLog getSpecLogForSpecRepoAndFile(Repository repo, String specFilePath, List<PullRequest> openPullRequests) {
        var latestAgreedSpecItem = specService.getSpecItem(repo, specFilePath, LATEST_AGREED_BRANCH);

        var pullRequestsWithSpecFile = openPullRequests.stream().filter(pullRequest -> pullRequest.changesFile(repo, specFilePath));
        var proposedChanges = pullRequestsWithSpecFile.map(pullRequest -> createProposedSpecChangeFor(pullRequest, repo, specFilePath)).collect(Collectors.toList());

        return new SpecLog(latestAgreedSpecItem, proposedChanges);
    }

    private ProposedSpecChange createProposedSpecChangeFor(PullRequest pullRequest, Repository repo, String specFilePath) {
        var changedSpecItem = specService.getSpecItem(repo, specFilePath, pullRequest.getBranchName());
        return new ProposedSpecChange(pullRequest, changedSpecItem);
    }
}
