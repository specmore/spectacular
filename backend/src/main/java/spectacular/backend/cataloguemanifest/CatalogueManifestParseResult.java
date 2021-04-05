package spectacular.backend.cataloguemanifest;

import javax.validation.constraints.NotNull;
import spectacular.backend.cataloguemanifest.model.CatalogueManifest;

public class CatalogueManifestParseResult {
  private final CatalogueManifest catalogueManifest;
  private final String error;

  protected CatalogueManifestParseResult(CatalogueManifest catalogueManifest, String error) {
    this.catalogueManifest = catalogueManifest;
    this.error = error;
  }

  public static CatalogueManifestParseResult createParseErrorResult(@NotNull String error) {
    return new CatalogueManifestParseResult(null, error);
  }

  public static CatalogueManifestParseResult createSuccessfulCatalogueManifestParseResult(@NotNull CatalogueManifest catalogueManifest) {
    return new CatalogueManifestParseResult(catalogueManifest, null);
  }

  public CatalogueManifest getCatalogueManifest() {
    return catalogueManifest;
  }

  public String getError() {
    return error;
  }
}
