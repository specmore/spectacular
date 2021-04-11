package spectacular.backend.cataloguemanifest.catalogueentry;

import static spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError.createConfigError;
import static spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError.createNotFoundError;

import java.net.URI;
import org.springframework.stereotype.Service;
import spectacular.backend.cataloguemanifest.CatalogueManifestProvider;
import spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError;
import spectacular.backend.cataloguemanifest.configurationitem.ResolveConfigurationItemResult;
import spectacular.backend.cataloguemanifest.model.Catalogue;
import spectacular.backend.cataloguemanifest.parse.CatalogueManifestParser;
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

  /**
   * Gets a catalogue manifest file and attempts to find and parse a catalogue entry in it for a given user.
   *
   * @param catalogueId an object containing the manifest file location and name of the catalogue entry
   * @param username the user that is trying access the catalogue
   * @return a GetCatalogueEntryConfigurationResult object with
   *     1. a successfully found and parsed catalogue entry
   *     2. an error if the catalogue entry could not be found or it was not parsable
   */
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

    return GetCatalogueEntryConfigurationResult.createSuccessfulResult(
        parseResult.getCatalogue(),
        catalogueManifestContent.getHtml_url(),
        catalogueId);
  }

  public static class GetCatalogueEntryConfigurationResult extends ResolveConfigurationItemResult {
    private final Catalogue catalogueEntry;
    private final URI manifestUri;
    private final CatalogueId catalogueId;

    private GetCatalogueEntryConfigurationResult(ConfigurationItemError error) {
      super(error);
      this.catalogueEntry = null;
      this.manifestUri = null;
      this.catalogueId = null;
    }

    private GetCatalogueEntryConfigurationResult(Catalogue catalogueEntry, URI manifestUri, CatalogueId catalogueId) {
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

    private static GetCatalogueEntryConfigurationResult createErrorResult(
        ConfigurationItemError error) {
      return new GetCatalogueEntryConfigurationResult(error);
    }

    private static GetCatalogueEntryConfigurationResult createSuccessfulResult(
        Catalogue catalogueEntry,
        URI manifestUri,
        CatalogueId catalogueId) {
      return new GetCatalogueEntryConfigurationResult(catalogueEntry, manifestUri,
          catalogueId);
    }
  }
}
