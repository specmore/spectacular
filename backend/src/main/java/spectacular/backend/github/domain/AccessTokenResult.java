package spectacular.backend.github.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

public class AccessTokenResult {
  private final String token;
  private final ZonedDateTime expirationDateTime;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public AccessTokenResult(@JsonProperty("token") String token, @JsonProperty("expires_at") ZonedDateTime expirationDateTime) {
    this.token = token;
    this.expirationDateTime = expirationDateTime;
  }

  public String getToken() {
    return token;
  }

  public ZonedDateTime getExpirationDateTime() {
    return expirationDateTime;
  }
}
