package spectacular.backend.github.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;

public class Repository {
  private final int id;
  private final String full_name;
  private final URI html_url;
  private final String default_branch;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public Repository(@JsonProperty("id") int id, @JsonProperty("full_name") String full_name, @JsonProperty("html_url") URI html_url,
                    @JsonProperty("default_branch") String default_branch) {
    this.id = id;
    this.full_name = full_name;
    this.html_url = html_url;
    this.default_branch = default_branch;
  }

  public int getId() {
    return id;
  }

  public String getFull_name() {
    return full_name;
  }

  public URI getHtml_url() {
    return html_url;
  }

  public String getDefault_branch() {
    return default_branch;
  }
}
