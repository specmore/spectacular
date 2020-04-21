package spectacular.backend.github.app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Account {
  private final String login;
  private final int id;
  private final String avatar_url;

  public Account(@JsonProperty("login") String login, @JsonProperty("id") int id,
                 @JsonProperty("avatar_url") String avatar_url) {
    this.login = login;
    this.id = id;
    this.avatar_url = avatar_url;
  }

  public String getLogin() {
    return login;
  }

  public int getId() {
    return id;
  }

  public String getAvatar_url() {
    return avatar_url;
  }
}
