package spectacular.backend.catalogues;

import java.util.Base64;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import spectacular.backend.common.Repository;

public class CatalogueId extends CatalogueManifestId {
  protected final String catalogueName;
  private String encoded;

  public CatalogueId(@NotNull Repository repository,
                     @NotNull String path,
                     @NotNull String catalogueName) {
    super(repository, path);
    this.catalogueName = catalogueName;
  }

  public String getCatalogueName() {
    return catalogueName;
  }

  public String getEncoded() {
    if (encoded == null) {
      var combined = String.join("/", this.repository.getNameWithOwner(), this.path, this.catalogueName);
      encoded = Base64.getEncoder().encodeToString(combined.getBytes());
    }
    return encoded;
  }

  public static CatalogueId createFrom(String encodedString) {
    byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
    var combined = new String(decodedBytes);
    int firstSlash = combined.indexOf("/");
    int secondSlash = combined.indexOf("/", firstSlash + 1);
    int afterPathSlash = 0;
    int fileExtensionAndSlash = combined.indexOf(".yml/");
    if (fileExtensionAndSlash >= 0) {
      afterPathSlash = fileExtensionAndSlash + 4;
    } else {
      fileExtensionAndSlash = combined.indexOf(".yaml/");
      afterPathSlash = fileExtensionAndSlash + 5;
    }

    var repository = Repository.createForNameWithOwner(combined.substring(0, secondSlash - 1));
    var path = combined.substring(secondSlash + 1, afterPathSlash -1);
    var name = combined.substring(afterPathSlash + 1);

    return new CatalogueId(repository, path, name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    CatalogueId that = (CatalogueId) o;
    return getCatalogueName().equals(that.getCatalogueName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getCatalogueName());
  }

  @Override
  public String toString() {
    return "CatalogueId{" +
        "catalogueName='" + catalogueName + '\'' +
        ", repository=" + repository +
        ", path='" + path + '\'' +
        '}';
  }
}
