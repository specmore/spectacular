package spectacular.backend.cataloguemanifest;

import spectacular.backend.cataloguemanifest.model.Catalogue;

public class CatalogueParseResult {
  private final Catalogue catalogue;
  private final String error;

  public CatalogueParseResult(Catalogue catalogue, String error) {
    this.catalogue = catalogue;
    this.error = error;
  }

  public Catalogue getCatalogue() {
    return catalogue;
  }

  public String getError() {
    return error;
  }
}
