package spectacular.backend.cataloguemanifest.parse;

import spectacular.backend.cataloguemanifest.model.Catalogue;
import spectacular.backend.github.domain.ContentItem;

public class FindAndParseCatalogueResult {
  private final ContentItem manifestContentItem;
  private final boolean isCatalogueEntryNotFound;
  private final boolean isCatalogueEntryContainsErrors;
  private final Catalogue catalogue;
  private final String error;

  private FindAndParseCatalogueResult(ContentItem manifestContentItem, boolean isCatalogueEntryNotFound,
                                      boolean isCatalogueEntryContainsErrors, Catalogue catalogue,
                                      String error) {
    this.manifestContentItem = manifestContentItem;
    this.isCatalogueEntryNotFound = isCatalogueEntryNotFound;
    this.isCatalogueEntryContainsErrors = isCatalogueEntryContainsErrors;
    this.catalogue = catalogue;
    this.error = error;
  }

  public static FindAndParseCatalogueResult createCatalogueEntryNotFoundResult(ContentItem manifestContentItem) {
    return new FindAndParseCatalogueResult(manifestContentItem, true, false, null, null);
  }

  public static FindAndParseCatalogueResult createCatalogueEntryParseErrorResult(ContentItem manifestContentItem, String parseError) {
    return new FindAndParseCatalogueResult(manifestContentItem, false, true, null, parseError);
  }

  public static FindAndParseCatalogueResult createCatalogueEntryParsedResult(ContentItem manifestContentItem, Catalogue catalogue) {
    return new FindAndParseCatalogueResult(manifestContentItem, false, false, catalogue, null);
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

  public ContentItem getManifestContentItem() {
    return manifestContentItem;
  }
}
