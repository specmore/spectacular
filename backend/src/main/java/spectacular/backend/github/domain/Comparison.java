package spectacular.backend.github.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;

public class Comparison {
  private final URI html_url;
  private final String status;
  private final int ahead_by;
  private final int behind_by;
  private final int total_commits;

  public Comparison(@JsonProperty("html_url") URI html_url,
                    @JsonProperty("status") String status,
                    @JsonProperty("ahead_by") int ahead_by,
                    @JsonProperty("behind_by") int behind_by,
                    @JsonProperty("total_commits") int total_commits) {
    this.html_url = html_url;
    this.status = status;
    this.ahead_by = ahead_by;
    this.behind_by = behind_by;
    this.total_commits = total_commits;
  }

  public URI getHtml_url() {
    return html_url;
  }

  public String getStatus() {
    return status;
  }

  public int getAhead_by() {
    return ahead_by;
  }

  public int getBehind_by() {
    return behind_by;
  }

  public int getTotal_commits() {
    return total_commits;
  }
}
