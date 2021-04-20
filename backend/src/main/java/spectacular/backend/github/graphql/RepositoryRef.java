package spectacular.backend.github.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RepositoryRef extends Ref {
  private final String name;
  private final Commit target;

  /**
   * A git ref object that is associated to a repository.
   * @param name the name of the ref
   * @param repository the repository it is associated to
   * @param target the git object it is referencing
   */
  public RepositoryRef(@JsonProperty("name") String name,
                       @JsonProperty("repository") Repository repository,
                       @JsonProperty("target") Commit target
  ) {
    super(name, repository);
    this.name = name;
    this.target = target;
  }

  public String getName() {
    return name;
  }

  public Commit getTarget() {
    return target;
  }
}
