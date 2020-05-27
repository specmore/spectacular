package spectacular.backend.common;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import spectacular.backend.github.domain.SearchCodeResultItem;

public class CatalogueManifestId {
  protected final RepositoryId repositoryId;
  protected final String path;
  private String fullPath;

  public CatalogueManifestId(@NotNull RepositoryId repositoryId, @NotNull String path) {
    this.repositoryId = repositoryId;
    this.path = path;
  }

  public RepositoryId getRepositoryId() {
    return repositoryId;
  }

  public String getPath() {
    return path;
  }

  /**
   * Calculates a combined full path representing the location of this CatalogueManifest
   * built up from joining the repository full name and manifest file path.
   *
   * @return a combined string of the manifest repository and file path
   */
  public String getFullPath() {
    if (fullPath == null) {
      fullPath = String.join("/", repositoryId.getNameWithOwner(), path);
    }
    return fullPath;
  }

  public static CatalogueManifestId createFrom(SearchCodeResultItem searchCodeResultItem) {
    return new CatalogueManifestId(RepositoryId.createRepositoryFrom(searchCodeResultItem.getRepository()), searchCodeResultItem.getPath());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CatalogueManifestId that = (CatalogueManifestId) o;
    return getRepositoryId().equals(that.getRepositoryId()) &&
        getPath().equals(that.getPath());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getRepositoryId(), getPath());
  }

  @Override
  public String toString() {
    return "CatalogueManifestId{" +
        "repository=" + repositoryId +
        ", path='" + path + '\'' +
        '}';
  }
}
