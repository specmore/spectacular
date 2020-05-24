package spectacular.backend.catalogues;

import java.util.Base64;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import spectacular.backend.common.Repository;
import spectacular.backend.github.domain.SearchCodeResultItem;

public class CatalogueManifestId {
  private final Repository repository;
  private final String path;
  private final String encoded;

  public CatalogueManifestId(@NotNull Repository repository, @NotNull String path) {
    this.repository = repository;
    this.path = path;

    var combinedLocation = repository.getNameWithOwner() + "/" + path;
    this.encoded = Base64.getEncoder().encodeToString(combinedLocation.getBytes());
  }

  public Repository getRepository() {
    return repository;
  }

  public String getPath() {
    return path;
  }

  public String getEncoded() {
    return encoded;
  }

  public static CatalogueManifestId createFrom(String encodedString) {
    byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
    var combinedLocation = new String(decodedBytes);
    int firstSlash = combinedLocation.indexOf("/");
    int secondSlash = combinedLocation.indexOf("/", firstSlash + 1);

    var repository = Repository.createForNameWithOwner(combinedLocation.substring(0, secondSlash - 1));
    var path = combinedLocation.substring(secondSlash + 1);

    return new CatalogueManifestId(repository, path);
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
        ", encoded='" + encoded + '\'' +
        '}';
  }
}
