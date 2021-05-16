package spectacular.backend.catalogues;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import spectacular.backend.api.model.Catalogue;
import spectacular.backend.cataloguemanifest.catalogueentry.CatalogueEntryConfigurationResolver;
import spectacular.backend.cataloguemanifest.model.CatalogueManifest;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.common.CatalogueManifestId;

@Component
public class CatalogueMapper {
  private static final Logger logger = LoggerFactory.getLogger(CatalogueMapper.class);

  /**
   * Maps a resolved manifest catalogue entry to an API Catalogue model.
   *
   * @param catalogueEntryResult a resolved catalogue entry
   * @return a Catalogue API model
   */
  public Catalogue mapCatalogue(CatalogueEntryConfigurationResolver.GetCatalogueEntryConfigurationResult catalogueEntryResult) {
    if (catalogueEntryResult.hasError()) {
      return createForParseError(catalogueEntryResult.getError().getMessage(), catalogueEntryResult.getCatalogueId());
    }

    return mapCatalogue(catalogueEntryResult.getCatalogueEntry(),
        catalogueEntryResult.getCatalogueId(),
        catalogueEntryResult.getManifestUri(),
        catalogueEntryResult.getCatalogueId().getCatalogueName(),
        catalogueEntryResult.getTopics());
  }

  /**
   * Maps a manifest catalogue item to an API Catalogue model.
   *
   * @param catalogue the manifest catalogue item
   * @param manifestId the id of the manifest
   * @param catalogueName the name of the catalogue item
   * @return a Catalogue API model
   */
  public Catalogue mapCatalogue(
      spectacular.backend.cataloguemanifest.model.Catalogue catalogue,
      CatalogueManifestId manifestId,
      URI manifestUrl,
      String catalogueName,
      List<String> topics) {
    var interfaceCount = catalogue.getInterfaces() == null ?
        0 :
        (int) catalogue.getInterfaces().getAdditionalProperties().values().stream().filter(Objects::nonNull).count();
    return new Catalogue()
        .encodedId(manifestId.createCatalogueId(catalogueName).getCombined().getBytes())
        .fullPath(manifestId.getFullPath())
        .name(catalogueName)
        .title(catalogue.getTitle())
        .description(catalogue.getDescription())
        .htmlUrl(manifestUrl)
        .interfaceCount(interfaceCount)
        .topics(topics);
  }

  /**
   * Creates an API Catalogue model that represents a fail parsing of an entire Catalogue Manifest file.
   *
   * @param parseError the error that occurred while parsing the manifest file
   * @param manifestId the id of the manifest file being parsed
   * @return a Catalogue API model
   */
  public Catalogue createForParseError(String parseError, CatalogueManifestId manifestId) {
    return new Catalogue()
        .fullPath(manifestId.getFullPath())
        .name("error")
        .parseError(parseError);
  }

  /**
   * Creates an API Catalogue model that represents a fail parsing of a specific catalogue in a Catalogue Manifest file.
   * @param parseError the error that occurred while parsing the manifest file
   * @param catalogueId the id of the catalogue being parsed
   * @return a Catalogue API model
   */
  public Catalogue createForParseError(String parseError, CatalogueId catalogueId) {
    return new Catalogue()
        .fullPath(catalogueId.getFullPath())
        .name(catalogueId.getCatalogueName())
        .parseError(parseError);
  }
}
