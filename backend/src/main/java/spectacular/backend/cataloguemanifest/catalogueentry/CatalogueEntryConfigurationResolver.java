package spectacular.backend.cataloguemanifest.catalogueentry;

import static spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError.createConfigError;
import static spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError.createNotFoundError;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import spectacular.backend.cataloguemanifest.CatalogueManifestProvider;
import spectacular.backend.cataloguemanifest.GetCatalogueManifestFileContentResult;
import spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError;
import spectacular.backend.cataloguemanifest.configurationitem.ResolveConfigurationItemResult;
import spectacular.backend.cataloguemanifest.model.Catalogue;
import spectacular.backend.cataloguemanifest.parse.CatalogueManifestParser;
import spectacular.backend.common.CatalogueId;

@Service
public class CatalogueEntryConfigurationResolver {
  private static final Logger logger = LoggerFactory.getLogger(CatalogueEntryConfigurationResolver.class);

  private final CatalogueManifestParser catalogueManifestParser;
  private final CatalogueManifestProvider catalogueManifestProvider;

  public CatalogueEntryConfigurationResolver(CatalogueManifestParser catalogueManifestParser,
                                             CatalogueManifestProvider catalogueManifestProvider) {
    this.catalogueManifestParser = catalogueManifestParser;
    this.catalogueManifestProvider = catalogueManifestProvider;
  }

  /**
   * Finds all catalogue manifest files for an org that a user has access to and creates a single list of all their catalogue entries
   * parsed from their file contents.
   *
   * @param orgName the org to search for files in
   * @param username the user to check for access on
   * @return a list of GetCatalogueEntryConfigurationResult objects with
   *     1. a successfully found and parsed catalogue entry
   *     2. an error if the catalogue manifest file was not parsable
   */
  public List<GetCatalogueEntryConfigurationResult> findCataloguesForOrgAndUser(String orgName, String username) {
    var manifestFiles = this.catalogueManifestProvider.findCatalogueManifestsForOrg(orgName, username);
    return manifestFiles.stream()
        .map(this::getCatalogueEntriesFrom)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
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
          createNotFoundError("Catalogue manifest file not found: " + catalogueId.getFullPath()), catalogueId);
    }

    var catalogueManifestContent = getCatalogueManifestFileContentResult.getCatalogueManifestContent();
    var parseResult = catalogueManifestParser.findAndParseCatalogueInManifestFileContents(catalogueManifestContent,
        catalogueId.getCatalogueName());

    if (parseResult.isCatalogueEntryNotFound()) {
      return GetCatalogueEntryConfigurationResult.createErrorResult(
          createNotFoundError("Catalogue entry in manifest file not found: " + catalogueId.getCombined()), catalogueId);
    }

    if (parseResult.isCatalogueEntryContainsError()) {
      return GetCatalogueEntryConfigurationResult.createErrorResult(
          createConfigError("Catalogue entry in manifest file: " + catalogueId.getCombined() +
              ", has parse error: " + parseResult.getError()), catalogueId);
    }

    return GetCatalogueEntryConfigurationResult.createSuccessfulResult(
        parseResult.getCatalogue(),
        catalogueManifestContent.getHtml_url(),
        catalogueId);
  }

  private List<GetCatalogueEntryConfigurationResult> getCatalogueEntriesFrom(
      GetCatalogueManifestFileContentResult getCatalogueManifestFileContentResult) {
    var manifestId = getCatalogueManifestFileContentResult.getCatalogueManifestId();

    if (getCatalogueManifestFileContentResult.isFileNotFoundResult()) {
      logger.warn("A manifest file was found during a search but the actual file contents could not subsequently be found for: " +
          manifestId.getFullPath());
      return Collections.emptyList();
    }

    var fileContentItem = getCatalogueManifestFileContentResult.getCatalogueManifestContent();
    var parseResult = catalogueManifestParser.parseManifestFileContentItem(fileContentItem);

    if (parseResult.getError() != null) {
      var entryConfigurationResult = GetCatalogueEntryConfigurationResult.createErrorResult(
          createConfigError(parseResult.getError()), CatalogueId.createFrom(manifestId, "error"));
      return Collections.singletonList(entryConfigurationResult);
    }

    var catalogueManifest = parseResult.getCatalogueManifest();
    if (catalogueManifest.getCatalogues() == null) {
      logger.debug("A manifest file was found during a search but doesn't contain any catalogue entries: " + manifestId.getFullPath());
      return Collections.emptyList();
    }

    return catalogueManifest.getCatalogues().getAdditionalProperties().entrySet().stream()
        .map(catalogueEntry -> {
          var catalogueId = CatalogueId.createFrom(manifestId, catalogueEntry.getKey());
          return GetCatalogueEntryConfigurationResult.createSuccessfulResult(
              catalogueEntry.getValue(),
              fileContentItem.getHtml_url(),
              catalogueId);
        })
        .collect(Collectors.toList());
  }

  public static class GetCatalogueEntryConfigurationResult extends ResolveConfigurationItemResult {
    private final Catalogue catalogueEntry;
    private final URI manifestUri;
    private final CatalogueId catalogueId;

    private GetCatalogueEntryConfigurationResult(ConfigurationItemError error, CatalogueId catalogueId) {
      super(error);
      this.catalogueEntry = null;
      this.manifestUri = null;
      this.catalogueId = catalogueId;
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

    private static GetCatalogueEntryConfigurationResult createErrorResult(ConfigurationItemError error, CatalogueId catalogueId) {
      return new GetCatalogueEntryConfigurationResult(error, catalogueId);
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
