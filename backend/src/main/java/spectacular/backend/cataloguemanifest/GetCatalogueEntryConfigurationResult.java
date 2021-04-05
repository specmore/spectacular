package spectacular.backend.cataloguemanifest;

import spectacular.backend.cataloguemanifest.model.Catalogue;

public class GetCatalogueEntryConfigurationResult extends GetCatalogueManifestConfigurationItemResult {
  private final Catalogue catalogueEntry;

  protected GetCatalogueEntryConfigurationResult(GetCatalogueManifestConfigurationItemError error) {
    super(error);
    this.catalogueEntry = null;
  }

  protected GetCatalogueEntryConfigurationResult(Catalogue catalogueEntry) {
    super(null);
    this.catalogueEntry = catalogueEntry;
  }

  public Catalogue getCatalogueEntry() {
    return catalogueEntry;
  }

  public static GetCatalogueEntryConfigurationResult createErrorResult(GetCatalogueManifestConfigurationItemError error) {
    return new GetCatalogueEntryConfigurationResult(error);
  }

  public static GetCatalogueEntryConfigurationResult createSuccessfulResult(Catalogue catalogueEntry) {
    return new GetCatalogueEntryConfigurationResult(catalogueEntry);
  }
}
