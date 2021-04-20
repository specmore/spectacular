package spectacular.backend.cataloguemanifest;

import spectacular.backend.common.CatalogueManifestId;
import spectacular.backend.github.domain.ContentItem;

public class GetCatalogueManifestFileContentResult {
  private final CatalogueManifestId catalogueManifestId;
  private final ContentItem catalogueManifestContent;
  private final boolean isFileNotFoundResult;

  private GetCatalogueManifestFileContentResult(CatalogueManifestId catalogueManifestId,
                                               ContentItem catalogueManifestContent, boolean isFileNotFoundResult) {
    this.catalogueManifestId = catalogueManifestId;
    this.catalogueManifestContent = catalogueManifestContent;
    this.isFileNotFoundResult = isFileNotFoundResult;
  }

  public static GetCatalogueManifestFileContentResult createSuccessfulResult(CatalogueManifestId catalogueManifestId,
                                                                      ContentItem catalogueManifestContent) {
    return new GetCatalogueManifestFileContentResult(catalogueManifestId, catalogueManifestContent, false);
  }

  public static GetCatalogueManifestFileContentResult createNotFoundResult(CatalogueManifestId catalogueManifestId) {
    return new GetCatalogueManifestFileContentResult(catalogueManifestId, null, true);
  }

  public CatalogueManifestId getCatalogueManifestId() {
    return catalogueManifestId;
  }

  public ContentItem getCatalogueManifestContent() {
    return catalogueManifestContent;
  }

  public boolean isFileNotFoundResult() {
    return isFileNotFoundResult;
  }
}
