package spectacular.backend.github.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserAccessTokenResult {
  private final String accessToken;
  private final long expiresIn;
  private final String tokenType;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public UserAccessTokenResult(@JsonProperty("access_token") String accessToken,
                               @JsonProperty("expires_in") long expiresIn,
                               @JsonProperty("token_type") String tokenType) {
    this.accessToken = accessToken;
    this.expiresIn = expiresIn;
    this.tokenType = tokenType;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  public String getTokenType() {
    return tokenType;
  }
}
