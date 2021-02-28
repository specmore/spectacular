package spectacular.backend.specevolution;

import com.vdurmont.semver4j.Semver;
import spectacular.backend.github.refs.BranchRef;

public class ReleaseBranch {
  private final BranchRef branch;
  private final Semver version;

  public ReleaseBranch(BranchRef branch, Semver version) {
    this.branch = branch;
    this.version = version;
  }

  public BranchRef getBranch() {
    return branch;
  }

  public Semver getVersion() {
    return version;
  }
}
