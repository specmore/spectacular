package spectacular.backend.github.graphql;

public class GraphQlRequest {
  private final String query;

  public GraphQlRequest(String query) {
    this.query = query;
  }

  public String getQuery() {
    return query;
  }
}
