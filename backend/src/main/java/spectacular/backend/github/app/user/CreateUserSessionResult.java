package spectacular.backend.github.app.user;

import spectacular.backend.api.model.UserDetails;

public class CreateUserSessionResult {
  private final String userSessionToken;
  private final UserDetails userDetails;

  public CreateUserSessionResult(String userSessionToken, UserDetails userDetails) {
    this.userSessionToken = userSessionToken;
    this.userDetails = userDetails;
  }

  public String getUserSessionToken() {
    return userSessionToken;
  }

  public UserDetails getUserDetails() {
    return userDetails;
  }
}
