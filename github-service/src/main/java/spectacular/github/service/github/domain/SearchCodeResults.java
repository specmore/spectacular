package spectacular.github.service.github.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SearchCodeResults {
    private final int totalCount;
    private final List<SearchCodeResultItem> items;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public SearchCodeResults(@JsonProperty("total_count") int totalCount, @JsonProperty("items") List<SearchCodeResultItem> items) {
        this.totalCount = totalCount;
        this.items = items;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<SearchCodeResultItem> getItems() {
        return items;
    }
}
