package spectacular.backend.github.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchCodeResultItem {
  private final String name;
  private final String path;
  private final String url;
  private final String gitUrl;
  private final String htmlUrl;
  private final Repository repository;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public SearchCodeResultItem(@JsonProperty("name") String name, @JsonProperty("path") String path,
                              @JsonProperty("url") String url,
                              @JsonProperty("git_url") String gitUrl,
                              @JsonProperty("html_url") String htmlUrl,
                              @JsonProperty("repository") Repository repository) {
    this.name = name;
    this.path = path;
    this.url = url;
    this.gitUrl = gitUrl;
    this.htmlUrl = htmlUrl;
    this.repository = repository;
  }

  public String getName() {
    return this.name;
  }

  public String getPath() {
    return path;
  }

  public String getUrl() {
    return url;
  }

  public String getGitUrl() {
    return gitUrl;
  }

  public String getHtmlUrl() {
    return htmlUrl;
  }

  public Repository getRepository() {
    return repository;
  }
}
