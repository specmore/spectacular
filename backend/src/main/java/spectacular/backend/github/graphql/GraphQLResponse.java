package spectacular.backend.github.graphql;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class GraphQLResponse {
    private final ResponseData data;
    private final JsonNode errors;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public GraphQLResponse(@JsonProperty("data") ResponseData data, @JsonProperty("errors") JsonNode errors) {
        this.data = data;
        this.errors = errors;
    }

    public ResponseData getData() {
        return data;
    }

    public JsonNode getErrors() {
        return errors;
    }
}
