package spectacular.backend.github.refs;

import spectacular.backend.github.graphql.RepositoryRef;

public class TagRef {
  private final String name;
  private final String commit;

  public TagRef(String name, String commit) {
    this.name = name;
    this.commit = commit;
  }

  /**
   * A factory method for creating TagRef objects from GraphQL data.
   * @param ref the graphql data
   * @return a newly constructed TagRef object
   */
  public static TagRef createTagRefFrom(RepositoryRef ref) {
    var name = ref.getName();
    var commit = ref.getTarget().getOid();
    return new TagRef(name, commit);
  }

  public String getName() {
    return name;
  }

  public String getCommit() {
    return commit;
  }
}
