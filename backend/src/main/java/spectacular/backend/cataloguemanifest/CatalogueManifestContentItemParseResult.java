package spectacular.backend.cataloguemanifest;

import javax.validation.constraints.NotNull;
import spectacular.backend.cataloguemanifest.model.CatalogueManifest;
import spectacular.backend.github.domain.ContentItem;

public class CatalogueManifestContentItemParseResult extends CatalogueManifestParseResult {
  private final ContentItem manifestContentItem;

  private CatalogueManifestContentItemParseResult(CatalogueManifest catalogueManifest, String error, ContentItem manifestContentItem) {
    super(catalogueManifest, error);
    this.manifestContentItem = manifestContentItem;
  }

  public static CatalogueManifestContentItemParseResult createParseErrorResult(@NotNull String error, ContentItem manifestContentItem) {
    return new CatalogueManifestContentItemParseResult(null, error, manifestContentItem);
  }

  public static CatalogueManifestContentItemParseResult createSuccessfulParseResult(@NotNull CatalogueManifest catalogueManifest,
                                                                                    ContentItem manifestContentItem) {
    return new CatalogueManifestContentItemParseResult(catalogueManifest, null, manifestContentItem);
  }

  public static CatalogueManifestContentItemParseResult createFrom(@NotNull CatalogueManifestParseResult result,
                                                                   ContentItem manifestContentItem) {
    return new CatalogueManifestContentItemParseResult(result.getCatalogueManifest(), result.getError(), manifestContentItem);
  }

  public ContentItem getManifestContentItem() {
    return manifestContentItem;
  }
}
