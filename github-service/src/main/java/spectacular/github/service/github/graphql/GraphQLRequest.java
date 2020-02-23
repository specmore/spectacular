package spectacular.github.service.github.graphql;

public class GraphQLRequest {
    private final String query;

    public GraphQLRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
