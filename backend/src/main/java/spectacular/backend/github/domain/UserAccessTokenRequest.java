package spectacular.backend.github.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserAccessTokenRequest {
  private final String clientId;
  private final String clientSecret;
  private final String code;

  public UserAccessTokenRequest(String clientId, String clientSecret, String code) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.code = code;
  }

  @JsonProperty("client_id")
  public String getClientId() {
    return clientId;
  }

  @JsonProperty("client_secret")
  public String getClientSecret() {
    return clientSecret;
  }

  @JsonProperty("code")
  public String getCode() {
    return code;
  }
}
