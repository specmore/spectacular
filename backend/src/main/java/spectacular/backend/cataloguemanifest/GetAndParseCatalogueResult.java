package spectacular.backend.cataloguemanifest;

import java.net.URI;
import spectacular.backend.github.domain.ContentItem;

public class GetAndParseCatalogueResult {
  private final boolean isCatalogueManifestFileExists;
  private final URI catalogueManifestFileHtmlUrl;
  private final FindAndParseCatalogueResult catalogueParseResult;

  private GetAndParseCatalogueResult(boolean isCatalogueManifestFileExists, URI catalogueManifestFileHtmlUrl,
                                     FindAndParseCatalogueResult catalogueParseResult) {
    this.isCatalogueManifestFileExists = isCatalogueManifestFileExists;
    this.catalogueManifestFileHtmlUrl = catalogueManifestFileHtmlUrl;
    this.catalogueParseResult = catalogueParseResult;
  }

  public static GetAndParseCatalogueResult createFileNotFoundResult() {
    return new GetAndParseCatalogueResult(false, null, null);
  }

  public static GetAndParseCatalogueResult createFoundAndParsedResult(ContentItem fileContentItem,
                                                                      FindAndParseCatalogueResult catalogueParseResult) {
    return new GetAndParseCatalogueResult(true, fileContentItem.getHtml_url(), catalogueParseResult);
  }

  public FindAndParseCatalogueResult getCatalogueParseResult() {
    return catalogueParseResult;
  }

  public URI getCatalogueManifestFileHtmlUrl() {
    return catalogueManifestFileHtmlUrl;
  }

  public boolean isCatalogueManifestFileExists() {
    return isCatalogueManifestFileExists;
  }
}
