package spectacular.backend.specs;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import spectacular.backend.api.mapper.PullRequestMapper;
import spectacular.backend.api.model.ChangeProposal;
import spectacular.backend.cataloguemanifest.SpecFileRepositoryResolver;
import spectacular.backend.cataloguemanifest.model.Catalogue;
import spectacular.backend.cataloguemanifest.model.Interface;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.pullrequests.PullRequest;
import spectacular.backend.github.pullrequests.PullRequestRepository;

@Service
public class SpecLogService {
  private static final String LATEST_AGREED_BRANCH = "master";

  private final SpecService specService;
  private final PullRequestRepository pullRequestRepository;

  public SpecLogService(SpecService specService, PullRequestRepository pullRequestRepository) {
    this.specService = specService;
    this.pullRequestRepository = pullRequestRepository;
  }

  /**
   * Gets the SpecLogs representing the interfaces described in a manifest catalogue item.
   *
   * @param catalogue the manifest catalogue item
   * @param catalogueId the full id of the catalogue
   * @return a List of SpecLog items
   */
  public List<spectacular.backend.api.model.SpecLog> getSpecLogsFor(Catalogue catalogue, CatalogueId catalogueId) {
    return catalogue.getInterfaces().getAdditionalProperties().entrySet().stream()
        .map(interfaceEntry -> createSpecLog(interfaceEntry, catalogueId))
        .collect(Collectors.toList());
  }

  private spectacular.backend.api.model.SpecLog createSpecLog(Map.Entry<String, Interface> interfaceEntry, CatalogueId catalogueId) {
    var specRepo = SpecFileRepositoryResolver.resolveSpecFileRepository(interfaceEntry.getValue(), catalogueId);
    var specFilePath = interfaceEntry.getValue().getSpecFile().getFilePath();
    var latestAgreedSpecItem = specService.getSpecItem(specRepo, specFilePath, LATEST_AGREED_BRANCH);
    var pullRequestsWithSpecFile = pullRequestRepository.getPullRequestsForRepoAndFile(specRepo, specFilePath);
    var proposedChanges = pullRequestsWithSpecFile.stream()
        .map(pullRequest -> createChangeProposalFor(pullRequest, specFilePath))
        .collect(Collectors.toList());
    return new spectacular.backend.api.model.SpecLog()
        .interfaceName(interfaceEntry.getKey())
        .latestAgreed(latestAgreedSpecItem)
        .proposedChanges(proposedChanges);
  }

  private ChangeProposal createChangeProposalFor(PullRequest internalPullRequest, String specFilePath) {
    var changedSpecItem = specService.getSpecItem(internalPullRequest.getRepositoryId(), specFilePath, internalPullRequest.getBranchName());
    spectacular.backend.api.model.PullRequest pullRequest = PullRequestMapper.mapGitHubPullRequest(internalPullRequest);
    var id = pullRequest.getNumber();
    return new ChangeProposal()
        .id(id)
        .specItem(changedSpecItem)
        .pullRequest(pullRequest);
  }
}
