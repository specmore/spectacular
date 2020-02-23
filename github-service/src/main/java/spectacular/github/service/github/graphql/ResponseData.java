package spectacular.github.service.github.graphql;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseData {
    private final Repository repository;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ResponseData(@JsonProperty("repository") Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }
}