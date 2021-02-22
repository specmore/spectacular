package spectacular.backend.specevolution;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import spectacular.backend.api.model.EvolutionItem;
import spectacular.backend.api.model.TagEvolutionItem;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.domain.Comparison;
import spectacular.backend.github.domain.Tag;
import spectacular.backend.interfaces.InterfaceService;

@Service
public class BranchEvolutionBuilder {
  private final RestApiClient restApiClient;

  public BranchEvolutionBuilder(RestApiClient restApiClient) {
    this.restApiClient = restApiClient;
  }

  public List<EvolutionItem> generateEvolutionItems(RepositoryId fileRepo, String branchName, Collection<Tag> tags) {
    var mainBranchTagComparisons = tags.stream()
        .map(tag -> new BranchTagComparision(tag, branchName, this.restApiClient.getComparison(fileRepo, branchName, tag.getName())))
        .filter(branchTagComparision -> branchTagComparision.getAheadBy() == 0)
        .sorted(Comparator.comparingInt(BranchTagComparision::getBehindBy))
        .collect(Collectors.toList());

    // what is the html url for the tag? Do we try get the contents item just to get the Url or do we guess it?
    // what is the main branch?
    // filter the tags by pattern
    // order by semver or commits behind?
    List<EvolutionItem> mainBranchTagEvolutionItems = mainBranchTagComparisons.stream()
        .map(branchTagComparision -> new TagEvolutionItem().tag(branchTagComparision.getTag().getName()))
        .collect(Collectors.toList());

    return mainBranchTagEvolutionItems;
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
