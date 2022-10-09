package spectacular.backend.github.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GetInstallationsResult {
  private final int totalCount;
  private final List<Installation> installations;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public GetInstallationsResult(@JsonProperty("total_count") int totalCount,
                                @JsonProperty("installations") List<Installation> installations) {
    this.totalCount = totalCount;
    this.installations = installations;
  }

  public List<Installation> getInstallations() {
    return installations;
  }

  public int getTotalCount() {
    return totalCount;
  }
}
