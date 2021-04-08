package spectacular.backend.cataloguemanifest.parse;

import javax.validation.constraints.NotNull;
import spectacular.backend.cataloguemanifest.model.CatalogueManifest;
import spectacular.backend.github.domain.ContentItem;

public class CatalogueManifestContentItemParseResult {
  private final CatalogueManifest catalogueManifest;
  private final String error;
  private final ContentItem manifestContentItem;

  private CatalogueManifestContentItemParseResult(CatalogueManifest catalogueManifest, String error, ContentItem manifestContentItem) {
    this.catalogueManifest = catalogueManifest;
    this.error = error;
    this.manifestContentItem = manifestContentItem;
  }

  public static CatalogueManifestContentItemParseResult createParseErrorResult(@NotNull String error, ContentItem manifestContentItem) {
    return new CatalogueManifestContentItemParseResult(null, error, manifestContentItem);
  }

  public static CatalogueManifestContentItemParseResult createSuccessfulParseResult(@NotNull CatalogueManifest catalogueManifest,
                                                                                    ContentItem manifestContentItem) {
    return new CatalogueManifestContentItemParseResult(catalogueManifest, null, manifestContentItem);
  }

  public CatalogueManifest getCatalogueManifest() {
    return catalogueManifest;
  }

  public String getError() {
    return error;
  }

  public ContentItem getManifestContentItem() {
    return manifestContentItem;
  }
}
