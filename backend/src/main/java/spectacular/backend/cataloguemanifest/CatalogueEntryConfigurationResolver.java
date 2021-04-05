package spectacular.backend.cataloguemanifest;

import static spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemError.createConfigError;
import static spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemError.createNotFoundError;

import org.springframework.stereotype.Service;
import spectacular.backend.common.CatalogueId;

@Service
public class CatalogueEntryConfigurationResolver {
  private final CatalogueManifestParser catalogueManifestParser;
  private final CatalogueManifestProvider catalogueManifestProvider;

  public CatalogueEntryConfigurationResolver(CatalogueManifestParser catalogueManifestParser,
                                             CatalogueManifestProvider catalogueManifestProvider) {
    this.catalogueManifestParser = catalogueManifestParser;
    this.catalogueManifestProvider = catalogueManifestProvider;
  }

  public GetCatalogueEntryConfigurationResult getCatalogueEntryConfiguration(CatalogueId catalogueId, String username) {
    var getCatalogueManifestFileContentResult = catalogueManifestProvider.getCatalogueManifest(catalogueId, username);

    if (getCatalogueManifestFileContentResult.isFileNotFoundResult()) {
      return GetCatalogueEntryConfigurationResult.createErrorResult(
          createNotFoundError("Catalogue manifest file not found: " + catalogueId.getFullPath()));
    }

    var catalogueManifestContent = getCatalogueManifestFileContentResult.getCatalogueManifestContent();
    var parseResult = catalogueManifestParser.findAndParseCatalogueInManifestFileContents(catalogueManifestContent,
        catalogueId.getCatalogueName());

    if (parseResult.isCatalogueEntryNotFound()) {
      return GetCatalogueEntryConfigurationResult.createErrorResult(
          createNotFoundError("Catalogue entry in manifest file not found: " + catalogueId.getCombined()));
    }

    if (parseResult.isCatalogueEntryContainsError()) {
      return GetCatalogueEntryConfigurationResult.createErrorResult(
          createConfigError("Catalogue entry in manifest file: " + catalogueId.getCombined() +
              ", has parse error: " + parseResult.getError()));
    }

    return GetCatalogueEntryConfigurationResult.createSuccessfulResult(parseResult.getCatalogue(), catalogueManifestContent.getHtml_url());
  }
}
