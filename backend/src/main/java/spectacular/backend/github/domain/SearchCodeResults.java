package spectacular.backend.github.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SearchCodeResults {
  private final int totalCount;
  private final List<SearchCodeResultItem> items;
  private final boolean isIncompleteResults;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public SearchCodeResults(@JsonProperty("total_count") int totalCount,
                           @JsonProperty("items") List<SearchCodeResultItem> items,
                           @JsonProperty("incomplete_results") boolean isIncompleteResults) {
    this.totalCount = totalCount;
    this.items = items;
    this.isIncompleteResults = isIncompleteResults;
  }

  public int getTotalCount() {
    return totalCount;
  }

  public List<SearchCodeResultItem> getItems() {
    return items;
  }

  public boolean isIncompleteResults() {
    return isIncompleteResults;
  }
}
