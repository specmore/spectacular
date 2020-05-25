package spectacular.backend.specs;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import spectacular.backend.api.mapper.PullRequestMapper;
import spectacular.backend.api.model.ChangeProposal;
import spectacular.backend.cataloguemanifest.model.Catalogue;
import spectacular.backend.cataloguemanifest.model.Interface;
import spectacular.backend.catalogues.CatalogueId;
import spectacular.backend.common.Repository;
import spectacular.backend.pullrequests.PullRequest;
import spectacular.backend.pullrequests.PullRequestService;

@Service
public class SpecLogService {
  private static final String LATEST_AGREED_BRANCH = "master";

  private final SpecService specService;
  private final PullRequestService pullRequestService;

  public SpecLogService(SpecService specService, PullRequestService pullRequestService) {
    this.specService = specService;
    this.pullRequestService = pullRequestService;
  }

  public List<spectacular.backend.api.model.SpecLog> getSpecLogsFor(Catalogue catalogue, CatalogueId catalogueId) {
    return catalogue.getInterfaces().getAdditionalProperties().entrySet().stream()
        .map(interfaceEntry -> createSpecLog(interfaceEntry, catalogueId))
        .collect(Collectors.toList());
  }

  private spectacular.backend.api.model.SpecLog createSpecLog(Map.Entry<String, Interface> interfaceEntry, CatalogueId catalogueId) {
    var specRepo = interfaceEntry.getValue().getSpecFile().getRepo() != null ? Repository.createForNameWithOwner(interfaceEntry.getValue().getSpecFile().getRepo()) : catalogueId.getRepository();
    var specFilePath = interfaceEntry.getValue().getSpecFile().getFilePath();
    var latestAgreedSpecItem = specService.getSpecItem(specRepo, specFilePath, LATEST_AGREED_BRANCH);
    var pullRequestsWithSpecFile = pullRequestService.getPullRequestsForRepoAndFile(specRepo, specFilePath);
    var proposedChanges = pullRequestsWithSpecFile.stream()
        .map(pullRequest -> createChangeProposalFor(pullRequest, specFilePath))
        .collect(Collectors.toList());
    return new spectacular.backend.api.model.SpecLog()
        .interfaceName(interfaceEntry.getKey())
        .latestAgreed(latestAgreedSpecItem)
        .proposedChanges(proposedChanges);
  }

  private ChangeProposal createChangeProposalFor(PullRequest internalPullRequest, String specFilePath) {
    var changedSpecItem = specService.getSpecItem(internalPullRequest.getRepository(), specFilePath, internalPullRequest.getBranchName());
    spectacular.backend.api.model.PullRequest pullRequest = PullRequestMapper.mapGitHubPullRequest(internalPullRequest);
    var id = pullRequest.getNumber();
    return new ChangeProposal()
        .id(id)
        .specItem(changedSpecItem)
        .pullRequest(pullRequest);
  }
}
