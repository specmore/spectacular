package spectacular.backend.specs;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import spectacular.backend.common.Repository;
import spectacular.backend.pullrequests.PullRequest;

@Service
public class SpecLogService {
  private static final String LATEST_AGREED_BRANCH = "master";

  private final SpecService specService;

  public SpecLogService(SpecService specService) {
    this.specService = specService;
  }

  /**
   * Gets all the information of the current state of the specified Spec File's evolution.
   * @param repo the repository the spec file belongs to
   * @param specFilePath the file path of the spec item
   * @param openPullRequests a list of the current open pull requests for the repository
   * @return a SpecLog item representing the current state of the Spec File's evolution.
   */
  public SpecLog getSpecLogForSpecRepoAndFile(Repository repo, String specFilePath, List<PullRequest> openPullRequests) {
    var latestAgreedSpecItem = specService.getSpecItem(repo, specFilePath, LATEST_AGREED_BRANCH);

    var pullRequestsWithSpecFile = openPullRequests.stream().filter(pullRequest -> pullRequest.changesFile(repo, specFilePath));
    var proposedChanges = pullRequestsWithSpecFile
        .map(pullRequest -> createProposedSpecChangeFor(pullRequest, repo, specFilePath))
        .collect(Collectors.toList());

    return new SpecLog(latestAgreedSpecItem, proposedChanges);
  }

  private ProposedSpecChange createProposedSpecChangeFor(PullRequest pullRequest, Repository repo, String specFilePath) {
    var changedSpecItem = specService.getSpecItem(repo, specFilePath, pullRequest.getBranchName());
    return new ProposedSpecChange(pullRequest, changedSpecItem);
  }
}
