package spectacular.backend.cataloguemanifest;

import java.net.URI;
import spectacular.backend.cataloguemanifest.model.Catalogue;

public class GetCatalogueEntryConfigurationResult extends GetCatalogueManifestConfigurationItemResult {
  private final Catalogue catalogueEntry;
  private final URI manifestUri;

  protected GetCatalogueEntryConfigurationResult(GetCatalogueManifestConfigurationItemError error) {
    super(error);
    this.catalogueEntry = null;
    this.manifestUri = null;
  }

  protected GetCatalogueEntryConfigurationResult(Catalogue catalogueEntry, URI manifestUri) {
    super(null);
    this.catalogueEntry = catalogueEntry;
    this.manifestUri = manifestUri;
  }

  public Catalogue getCatalogueEntry() {
    return catalogueEntry;
  }

  public URI getManifestUri() {
    return manifestUri;
  }

  public static GetCatalogueEntryConfigurationResult createErrorResult(GetCatalogueManifestConfigurationItemError error) {
    return new GetCatalogueEntryConfigurationResult(error);
  }

  public static GetCatalogueEntryConfigurationResult createSuccessfulResult(Catalogue catalogueEntry, URI manifestUri) {
    return new GetCatalogueEntryConfigurationResult(catalogueEntry, manifestUri);
  }
}
