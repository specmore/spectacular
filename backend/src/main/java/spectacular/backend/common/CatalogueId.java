package spectacular.backend.common;

import java.util.Base64;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import spectacular.backend.cataloguemanifest.model.Catalogue;

public class CatalogueId extends CatalogueManifestId {
  protected final String catalogueName;
  private String combined;

  public CatalogueId(@NotNull RepositoryId repository,
                     @NotNull String path,
                     @NotNull String catalogueName) {
    super(repository, path);
    this.catalogueName = catalogueName;
  }

  public String getCatalogueName() {
    return catalogueName;
  }

  /**
   * Calculates the combined id to represent this catalogue.
   *
   * @return a combined string representation of this catalogue id
   */
  public String getCombined() {
    if (combined == null) {
      combined = String.join("/", this.repositoryId.getNameWithOwner(), this.path, this.catalogueName);
    }
    return combined;
  }

  public static CatalogueId createFrom(CatalogueManifestId catalogueManifestId, String catalogueName) {
    return new CatalogueId(catalogueManifestId.getRepositoryId(), catalogueManifestId.getPath(), catalogueName);
  }

  /**
   * Creates a CatalogueId from the values in a combined string.
   *
   * @param combined a combined string of all the catalogue id values
   * @return a CatalogueId representing the values stored in the combined string
   */
  public static CatalogueId createFrom(String combined) {
    if (combined == null) {
      throw new IllegalArgumentException("CatalogueId combined string cannot be null");
    }

    int firstSlash = combined.indexOf("/");
    int secondSlash = combined.indexOf("/", firstSlash + 1);
    if (firstSlash < 0 || secondSlash < 0) {
      throw new IllegalArgumentException("Encoded CatalogueId does not start a repository Id with Owner/Name pattern.");
    }
    int afterPathSlash = 0;
    int fileExtensionAndSlash = combined.indexOf(".yml/");
    if (fileExtensionAndSlash >= 0) {
      afterPathSlash = fileExtensionAndSlash + 4;
    } else {
      fileExtensionAndSlash = combined.indexOf(".yaml/");
      afterPathSlash = fileExtensionAndSlash + 5;
    }
    if (fileExtensionAndSlash < 0) {
      throw new IllegalArgumentException("Encoded CatalogueId is missing a manifest file path and extension after repository Id.");
    }

    var repository = RepositoryId.createForNameWithOwner(combined.substring(0, secondSlash));
    var path = combined.substring(secondSlash + 1, afterPathSlash);
    var name = combined.substring(afterPathSlash + 1);

    if (name.isBlank()) {
      throw new IllegalArgumentException("Encoded CatalogueId is missing a catalogue name after the manifest  file path.");
    }


    return new CatalogueId(repository, path, name);
  }

  public static CatalogueId createFromBase64(byte[] encodedId) {
    var decodedBytes = Base64.getDecoder().decode(encodedId);
    var combinedId = new String(decodedBytes);
    return createFrom(combinedId);
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
        ", repositoryId=" + repositoryId +
        ", path='" + path + '\'' +
        '}';
  }
}
