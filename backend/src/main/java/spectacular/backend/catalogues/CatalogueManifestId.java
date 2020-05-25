package spectacular.backend.catalogues;

import java.util.Base64;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import spectacular.backend.common.Repository;
import spectacular.backend.github.domain.SearchCodeResultItem;

public class CatalogueManifestId {
  protected final Repository repository;
  protected final String path;
  private String fullPath;

  public CatalogueManifestId(@NotNull Repository repository, @NotNull String path) {
    this.repository = repository;
    this.path = path;
  }

  public Repository getRepository() {
    return repository;
  }

  public String getPath() {
    return path;
  }

  public String getFullPath() {
    if (fullPath == null) {
      fullPath = String.join("/", repository.getNameWithOwner(), path);
    }
    return fullPath;
  }

  public static CatalogueManifestId createFrom(SearchCodeResultItem searchCodeResultItem) {
    return new CatalogueManifestId(Repository.createRepositoryFrom(searchCodeResultItem.getRepository()), searchCodeResultItem.getPath());
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
    return getRepository().equals(that.getRepository()) &&
        getPath().equals(that.getPath());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getRepository(), getPath());
  }

  @Override
  public String toString() {
    return "CatalogueManifestId{" +
        "repository=" + repository +
        ", path='" + path + '\'' +
        '}';
  }
}
