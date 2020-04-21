package spectacular.backend.github.app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Installation {
  private final int id;
  private final int app_id;
  private final Account account;

  public Installation(@JsonProperty("id") int id, @JsonProperty("app_id") int app_id,
                      @JsonProperty("account") Account account) {
    this.id = id;
    this.app_id = app_id;
    this.account = account;
  }

  public int getId() {
    return id;
  }

  public int getApp_id() {
    return app_id;
  }

  public Account getAccount() {
    return account;
  }
}
