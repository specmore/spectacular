package spectacular.backend.specevolution;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import spectacular.backend.api.mapper.PullRequestMapper;
import spectacular.backend.api.model.EvolutionItem;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.domain.Comparison;
import spectacular.backend.github.domain.Tag;
import spectacular.backend.github.pullrequests.PullRequest;
import spectacular.backend.interfaces.InterfaceService;

@Service
public class EvolutionBranchBuilder {
  private final RestApiClient restApiClient;

  public EvolutionBranchBuilder(RestApiClient restApiClient) {
    this.restApiClient = restApiClient;
  }

  /**
   * Generates evolution items for a given branch on the specification file's repository from the tags on the repository.
   *
   * @param fileRepo the identifier of the repository the specification file is located in
   * @param branchName the branch on the repository the evolution items are being calculated against
   * @param tags the collection of tags in the repository
   * @param pullRequests the collection of pull requests associated to this branch
   * @return a list of evolution items for the branch
   */
  public List<EvolutionItem> generateEvolutionItems(RepositoryId fileRepo,
                                                    String branchName,
                                                    Collection<Tag> tags,
                                                    Collection<PullRequest> pullRequests) {
    var branchTagComparisons = tags.stream()
        .map(tag -> new BranchTagComparision(tag, branchName, this.restApiClient.getComparison(fileRepo, branchName, tag.getName())))
        .filter(branchTagComparision -> branchTagComparision.getAheadBy() == 0)
        .sorted(Comparator.comparingInt(BranchTagComparision::getBehindBy))
        .collect(Collectors.toList());

    // what is the html url for the tag? Do we try get the contents item just to get the Url or do we guess it?
    var tagEvolutionItemsStream = branchTagComparisons.stream().map(this::createTagEvolutionItem);
    var pullRequestEvolutionItemsStream = pullRequests.stream().map(this::createPullRequestEvolutionItem);

    return Stream.concat(pullRequestEvolutionItemsStream, tagEvolutionItemsStream).collect(Collectors.toList());
  }

  private EvolutionItem createTagEvolutionItem(BranchTagComparision branchTagComparision) {
    return new EvolutionItem()
        .ref(branchTagComparision.getTag().getName())
        .tag(branchTagComparision.getTag().getName());
  }

  private EvolutionItem createPullRequestEvolutionItem(PullRequest pullRequest) {
    return new EvolutionItem()
        .ref(pullRequest.getBranchName())
        .pullRequest(PullRequestMapper.mapGitHubPullRequest(pullRequest));
  }

  private class BranchTagComparision {
    private final Tag tag;
    private final String branchName;
    private final Comparison comparison;

    private BranchTagComparision(Tag tag, String branchName, Comparison comparison) {
      this.tag = tag;
      this.branchName = branchName;
      this.comparison = comparison;
    }

    public Tag getTag() {
      return tag;
    }

    public String getBranchName() {
      return branchName;
    }

    public Comparison getComparison() {
      return comparison;
    }

    public int getBehindBy() {
      return comparison.getBehind_by();
    }

    public int getAheadBy() {
      return comparison.getAhead_by();
    }
  }
}
