package spectacular.backend.catalogues;

import java.util.Base64;
import javax.validation.constraints.NotNull;
import spectacular.backend.common.Repository;
import spectacular.backend.github.domain.SearchCodeResultItem;

public class CatalogueId {
  private final Repository repository;
  private final String path;
  private final String encoded;

  public CatalogueId(@NotNull Repository repository, @NotNull String path) {
    this.repository = repository;
    this.path = path;

    var combinedLocation = repository + "/" + path;
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

  public static CatalogueId createFrom(String encodedString) {
    byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
    var combinedLocation = new String(decodedBytes);
    int firstSlash = combinedLocation.indexOf("/");
    int secondSlash = combinedLocation.indexOf("/", firstSlash + 1);

    var repository = Repository.createForNameWithOwner(combinedLocation.substring(0, secondSlash - 1));
    var path = combinedLocation.substring(secondSlash + 1);

    return new CatalogueId(repository, path);
  }

  public static CatalogueId createFrom(SearchCodeResultItem searchCodeResultItem) {
    return new CatalogueId(Repository.createRepositoryFrom(searchCodeResultItem.getRepository()), searchCodeResultItem.getPath());
  }
}
