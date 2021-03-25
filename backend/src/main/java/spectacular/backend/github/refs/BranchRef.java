package spectacular.backend.github.refs;

import spectacular.backend.github.graphql.RepositoryRef;

public class BranchRef {
  private final String name;
  private final String commit;

  /**
   * An object that represents a Branch in a Git Repository.
   * @param name the name of the branch
   * @param commit the commit sha this branch currently points to
   */
  public BranchRef(String name, String commit) {
    this.name = name;
    this.commit = commit;
  }

  /**
   * A factory method for creating BranchRef objects from GraphQL data.
   * @param ref the graphql data
   * @return a newly constructed BranchRef object
   */
  public static BranchRef createBranchRefFrom(RepositoryRef ref) {
    var name = ref.getName();
    var commit = ref.getTarget().getOid();
    return new BranchRef(name, commit);
  }

  public String getName() {
    return name;
  }

  public String getCommit() {
    return commit;
  }
}
