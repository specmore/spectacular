package spectacular.github.service.github.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class AccessTokenResult {
    private final String token;
    private final LocalDateTime expirationDateTime;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public AccessTokenResult(@JsonProperty("token") String token, @JsonProperty("expires_at") LocalDateTime expirationDateTime) {
        this.token = token;
        this.expirationDateTime = expirationDateTime;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpirationDateTime() {
        return expirationDateTime;
    }
}
