package spectacular.github.service.github.app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppApiUnauthorizedError {
    private final String message;

    public AppApiUnauthorizedError(@JsonProperty("message") String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
