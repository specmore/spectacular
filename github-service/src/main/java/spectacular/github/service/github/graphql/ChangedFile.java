package spectacular.github.service.github.graphql;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangedFile {
    private final String path;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ChangedFile(@JsonProperty("path") String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
