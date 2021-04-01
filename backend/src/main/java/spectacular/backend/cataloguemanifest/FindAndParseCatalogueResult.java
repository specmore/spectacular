package spectacular.backend.cataloguemanifest;

import spectacular.backend.cataloguemanifest.model.Catalogue;

public class FindAndParseCatalogueResult {
  private final boolean isCatalogueEntryNotFound;
  private final boolean isCatalogueEntryContainsErrors;
  private final Catalogue catalogue;
  private final String error;

  private FindAndParseCatalogueResult(boolean isCatalogueEntryNotFound, boolean isCatalogueEntryContainsErrors, Catalogue catalogue,
                                      String error) {
    this.isCatalogueEntryNotFound = isCatalogueEntryNotFound;
    this.isCatalogueEntryContainsErrors = isCatalogueEntryContainsErrors;
    this.catalogue = catalogue;
    this.error = error;
  }

  public static FindAndParseCatalogueResult createCatalogueEntryNotFoundResult() {
    return new FindAndParseCatalogueResult(true, false, null, null);
  }

  public static FindAndParseCatalogueResult createCatalogueEntryParseErrorResult(String parseError) {
    return new FindAndParseCatalogueResult(false, true, null, parseError);
  }

  public static FindAndParseCatalogueResult createCatalogueEntryParsedResult(Catalogue catalogue) {
    return new FindAndParseCatalogueResult(false, false, catalogue, null);
  }

  public Catalogue getCatalogue() {
    return catalogue;
  }

  public String getError() {
    return error;
  }

  public boolean isCatalogueEntryNotFound() {
    return isCatalogueEntryNotFound;
  }

  public boolean isCatalogueEntryContainsError() {
    return isCatalogueEntryContainsErrors;
  }
}
