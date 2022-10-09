package spectacular.backend.github.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Account {
  private final String login;
  private final int id;
  private final String avatarUrl;
  private final String name;

  public Account(@JsonProperty("login") String login,
                 @JsonProperty("id") int id,
                 @JsonProperty("avatar_url") String avatarUrl,
                 @JsonProperty("name") String name) {
    this.login = login;
    this.id = id;
    this.avatarUrl = avatarUrl;
    this.name = name;
  }

  public String getLogin() {
    return login;
  }

  public int getId() {
    return id;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public String getName() {
    return name;
  }
}
