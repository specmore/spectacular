package spectacular.backend.specevolution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import spectacular.backend.api.model.EvolutionItem;
import spectacular.backend.api.model.SpecEvolution;
import spectacular.backend.api.model.SpecEvolutionSummary;
import spectacular.backend.api.model.SpecItem;

public class SpecEvolutionSummaryMapper {
  /**
   * Maps a full SpecEvolution into a SpecEvolutionSummary.
   *
   * @param specEvolution with all the evolution data
   * @return a SpecEvolutionSummary with a summarised version of the evolution data
   */
  public static SpecEvolutionSummary mapSpecEvolutionToSummary(SpecEvolution specEvolution) {
    List<EvolutionItem> allEvolutionItems = new ArrayList<>();
    SpecItem latestAgreedSpecItem = null;
    int agreedVersionTagCount = 0;

    if (specEvolution.getMain() != null) {
      var mainEvolutionBranch = specEvolution.getMain();
      var branchHeadEvolutionItem = mainEvolutionBranch.getEvolutionItems().stream().filter(ei -> ei.getBranchName() != null).findFirst();
      if (branchHeadEvolutionItem.isPresent()) {
        latestAgreedSpecItem = branchHeadEvolutionItem.get().getSpecItem();
      }

      allEvolutionItems.addAll(mainEvolutionBranch.getEvolutionItems());

      agreedVersionTagCount = (int) mainEvolutionBranch.getEvolutionItems().stream().filter(ei -> ei.getTags().size() > 0).count();
    }

    var upcomingReleaseCount = specEvolution.getReleases().size();

    var allReleaseBranchEvolutionItemsStream = specEvolution.getReleases().stream().flatMap(eb -> eb.getEvolutionItems().stream());
    allEvolutionItems.addAll(allReleaseBranchEvolutionItemsStream.collect(Collectors.toList()));

    var pullRequestEvolutionItemCount = allEvolutionItems.stream().filter(ei -> ei.getPullRequest() != null).count();

    return new SpecEvolutionSummary()
        .interfaceName(specEvolution.getInterfaceName())
        .latestAgreed(latestAgreedSpecItem)
        .upcomingReleaseCount(upcomingReleaseCount)
        .proposedChangesCount((int) pullRequestEvolutionItemCount)
        .agreedVersionTagCount(agreedVersionTagCount);
  }
}
