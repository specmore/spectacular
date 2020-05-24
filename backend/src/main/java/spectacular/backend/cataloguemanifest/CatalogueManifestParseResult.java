package spectacular.backend.cataloguemanifest;

import spectacular.backend.cataloguemanifest.model.CatalogueManifest;

public class CatalogueManifestParseResult {
  private final CatalogueManifest catalogueManifest;
  private final String error;

  public CatalogueManifestParseResult(CatalogueManifest catalogueManifest, String error) {
    this.catalogueManifest = catalogueManifest;
    this.error = error;
  }

  public CatalogueManifest getCatalogueManifest() {
    return catalogueManifest;
  }

  public String getError() {
    return error;
  }
}
