package spectacular.backend.specevolution;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import spectacular.backend.api.mapper.PullRequestMapper;
import spectacular.backend.api.model.EvolutionItem;
import spectacular.backend.api.model.OpenApiSpecParseResult;
import spectacular.backend.api.model.SpecItem;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.WebPageUrlGenerator;
import spectacular.backend.github.domain.Comparison;
import spectacular.backend.github.pullrequests.PullRequest;
import spectacular.backend.github.refs.BranchRef;
import spectacular.backend.github.refs.TagRef;
import spectacular.backend.specs.SpecService;

@Service
public class EvolutionBranchBuilder {
  private static final Logger logger = LoggerFactory.getLogger(EvolutionBranchBuilder.class);
  private final RestApiClient restApiClient;
  private final SpecService specService;
  private final WebPageUrlGenerator webPageUrlGenerator;

  /**
   * A helper class for building spec evolution items for a given branch.
   *
   * @param restApiClient a github rest api client
   * @param specService a spec service for generating spec items
   * @param webPageUrlGenerator a service for generating github web page urls
   */
  public EvolutionBranchBuilder(RestApiClient restApiClient,
                                SpecService specService,
                                WebPageUrlGenerator webPageUrlGenerator) {
    this.restApiClient = restApiClient;
    this.specService = specService;
    this.webPageUrlGenerator = webPageUrlGenerator;
  }

  /**
   * Generates evolution items for a given branch on the specification file's repository from the tags on the repository.
   *
   * @param fileRepo the identifier of the repository the specification file is located in
   * @param specFilePath the location of the spec file in the repository
   * @param branch the branch on the repository the evolution items are being calculated against
   * @param tags the collection of tags in the repository
   * @param pullRequests the collection of pull requests associated to this branch
   * @return a list of evolution items for the branch
   */
  public List<EvolutionItem> generateEvolutionItems(RepositoryId fileRepo,
                                                    String specFilePath,
                                                    BranchRef branch,
                                                    Collection<TagRef> tags,
                                                    Collection<PullRequest> pullRequests) {
    var tagsOnBranchHead = tags.stream()
        .collect(Collectors.partitioningBy(tag -> tag.getCommit().equals(branch.getCommit())));

    var branchHeadEvolutionItem = createBranchHeadEvolutionItem(fileRepo, specFilePath, branch, tagsOnBranchHead.get(true));

    var branchTagComparisonsGroupedByCommitsBehind = tagsOnBranchHead.get(false).stream()
        .map(tag -> {
          var comparison = this.restApiClient.getComparison(fileRepo, branch.getName(), tag.getName());
          return new BranchTagComparison(tag, branch.getName(), comparison);
        })
        .filter(branchTagComparison -> branchTagComparison.getAheadBy() == 0)
        .collect(Collectors.groupingBy(BranchTagComparison::getBehindBy))
        .entrySet().stream()
        .sorted(Comparator.comparingInt(Map.Entry::getKey))
        .collect(Collectors.toList());

    var tagEvolutionItemsStream = branchTagComparisonsGroupedByCommitsBehind.stream()
        .map(branchTagComparisons -> this.createTagEvolutionItem(fileRepo, specFilePath, branchTagComparisons));

    var pullRequestEvolutionItemsStream = pullRequests.stream()
        .map(pullRequest -> this.createPullRequestEvolutionItem(fileRepo, specFilePath, pullRequest));

    var concat = Stream.of(pullRequestEvolutionItemsStream, Stream.of(branchHeadEvolutionItem), tagEvolutionItemsStream).flatMap(s -> s);

    return concat.collect(Collectors.toList());
  }

  private EvolutionItem createBranchHeadEvolutionItem(RepositoryId fileRepo,
                                                      String specFilePath,
                                                      BranchRef branch,
                                                      List<TagRef> tagsOnBranchHead) {
    var tags = tagsOnBranchHead.stream().map(TagRef::getName).collect(Collectors.toList());

    var specItem = this.specService.getSpecItem(fileRepo, specFilePath, branch.getName());

    return new EvolutionItem()
        .ref(branch.getName())
        .branchName(branch.getName())
        .tags(tags)
        .specItem(specItem);
  }

  private EvolutionItem createTagEvolutionItem(RepositoryId fileRepo,
                                               String specFilePath,
                                               Map.Entry<Integer, List<BranchTagComparison>> branchTagComparisons) {
    var numberOfTags = branchTagComparisons.getValue().size();
    var firstTag = branchTagComparisons.getValue().get(0);
    var ref = numberOfTags == 1 ? firstTag.getTag().getName() : firstTag.getTag().getCommit();
    var tags = branchTagComparisons.getValue().stream()
        .map(branchTagComparison -> branchTagComparison.getTag().getName())
        .collect(Collectors.toList());

    var urlOnlySpecItem = this.generateHtmlUrlOnlySpecItem(fileRepo, specFilePath, ref);

    return new EvolutionItem()
        .ref(ref)
        .tags(tags)
        .specItem(urlOnlySpecItem);
  }

  private EvolutionItem createPullRequestEvolutionItem(RepositoryId fileRepo,
                                                       String specFilePath,
                                                       PullRequest pullRequest) {
    var urlOnlySpecItem = this.generateHtmlUrlOnlySpecItem(fileRepo, specFilePath, pullRequest.getBranchName());

    return new EvolutionItem()
        .ref(pullRequest.getBranchName())
        .pullRequest(PullRequestMapper.mapGitHubPullRequest(pullRequest))
        .tags(Collections.emptyList())
        .specItem(urlOnlySpecItem);
  }

  private SpecItem generateHtmlUrlOnlySpecItem(RepositoryId fileRepo, String specFilePath, String ref) {
    URI specItemHtmlUrl = null;
    OpenApiSpecParseResult parseResult = null;
    try {
      specItemHtmlUrl = this.webPageUrlGenerator.generateContentPageUrl(fileRepo, ref, specFilePath);
    } catch (URISyntaxException e) {
      parseResult = new OpenApiSpecParseResult().addErrorsItem(e.getMessage());
      logger.error("An error occurred while generating an HTML URL for a spec item in an evolution branch", e);
    }

    return new SpecItem()
        .htmlUrl(specItemHtmlUrl)
        .parseResult(parseResult);
  }

  private class BranchTagComparison {
    private final TagRef tag;
    private final String branchName;
    private final Comparison comparison;

    private BranchTagComparison(TagRef tag, String branchName, Comparison comparison) {
      this.tag = tag;
      this.branchName = branchName;
      this.comparison = comparison;
    }

    public TagRef getTag() {
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
