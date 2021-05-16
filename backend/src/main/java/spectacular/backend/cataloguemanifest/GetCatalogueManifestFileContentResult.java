package spectacular.backend.cataloguemanifest;

import spectacular.backend.common.CatalogueManifestId;
import spectacular.backend.github.domain.ContentItem;
import spectacular.backend.github.domain.RepositoryTopics;

public class GetCatalogueManifestFileContentResult {
  private final CatalogueManifestId catalogueManifestId;
  private final ContentItem catalogueManifestContent;
  private final boolean isFileNotFoundResult;
  private final RepositoryTopics repositoryTopics;

  private GetCatalogueManifestFileContentResult(CatalogueManifestId catalogueManifestId,
                                                ContentItem catalogueManifestContent, boolean isFileNotFoundResult,
                                                RepositoryTopics repositoryTopics) {
    this.catalogueManifestId = catalogueManifestId;
    this.catalogueManifestContent = catalogueManifestContent;
    this.isFileNotFoundResult = isFileNotFoundResult;
    this.repositoryTopics = repositoryTopics;
  }

  public static GetCatalogueManifestFileContentResult createSuccessfulResult(CatalogueManifestId catalogueManifestId,
                                                                             ContentItem catalogueManifestContent,
                                                                             RepositoryTopics repositoryTopics) {
    return new GetCatalogueManifestFileContentResult(catalogueManifestId, catalogueManifestContent, false, repositoryTopics);
  }

  public static GetCatalogueManifestFileContentResult createNotFoundResult(CatalogueManifestId catalogueManifestId) {
    return new GetCatalogueManifestFileContentResult(catalogueManifestId, null, true, null);
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

  public RepositoryTopics getRepositoryTopics() {
    return repositoryTopics;
  }
}
