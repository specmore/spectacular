package spectacular.backend.cataloguemanifest.catalogueentry;

import java.net.URI;
import spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError;
import spectacular.backend.cataloguemanifest.configurationitem.ResolveConfigurationItemResult;
import spectacular.backend.cataloguemanifest.model.Catalogue;
import spectacular.backend.common.CatalogueId;

public class GetCatalogueEntryConfigurationResult extends ResolveConfigurationItemResult {
  private final Catalogue catalogueEntry;
  private final URI manifestUri;
  private final CatalogueId catalogueId;

  protected GetCatalogueEntryConfigurationResult(ConfigurationItemError error) {
    super(error);
    this.catalogueEntry = null;
    this.manifestUri = null;
    this.catalogueId = null;
  }

  protected GetCatalogueEntryConfigurationResult(Catalogue catalogueEntry, URI manifestUri, CatalogueId catalogueId) {
    super(null);
    this.catalogueEntry = catalogueEntry;
    this.manifestUri = manifestUri;
    this.catalogueId = catalogueId;
  }

  public Catalogue getCatalogueEntry() {
    return catalogueEntry;
  }

  public URI getManifestUri() {
    return manifestUri;
  }

  public CatalogueId getCatalogueId() {
    return catalogueId;
  }

  public static GetCatalogueEntryConfigurationResult createErrorResult(ConfigurationItemError error) {
    return new GetCatalogueEntryConfigurationResult(error);
  }

  public static GetCatalogueEntryConfigurationResult createSuccessfulResult(Catalogue catalogueEntry,
                                                                            URI manifestUri,
                                                                            CatalogueId catalogueId) {
    return new GetCatalogueEntryConfigurationResult(catalogueEntry, manifestUri, catalogueId);
  }
}
