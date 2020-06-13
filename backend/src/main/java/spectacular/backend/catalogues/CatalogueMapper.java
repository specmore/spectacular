package spectacular.backend.catalogues;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import spectacular.backend.api.model.Catalogue;
import spectacular.backend.cataloguemanifest.model.CatalogueManifest;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.common.CatalogueManifestId;

@Component
public class CatalogueMapper {
  private static final Logger logger = LoggerFactory.getLogger(CatalogueMapper.class);

  /**
   * Maps catalogue entries in a Catalogue Manifest file to API Catalogue objects.
   *
   * @param catalogueManifest the catalogue manifest with catalogue entries to be mapped
   * @param manifestId the id of the manifest file
   * @param manifestUrl the HTML URL location of the manifest file
   * @return a list of API Catalogue objects found in the manifest file
   */
  public List<Catalogue> mapCatalogueManifestEntries(
      CatalogueManifest catalogueManifest,
      CatalogueManifestId manifestId,
      URI manifestUrl) {
    return catalogueManifest.getCatalogues().getAdditionalProperties().entrySet().stream()
        .map(catalogueEntry -> this.mapCatalogueManifestEntry(catalogueEntry, manifestId, manifestUrl))
        .collect(Collectors.toList());
  }

  public Catalogue mapCatalogueManifestEntry(
      Map.Entry<String, spectacular.backend.cataloguemanifest.model.Catalogue> catalogueEntry,
      CatalogueManifestId manifestId,
      URI manifestUrl) {
    return mapCatalogue(catalogueEntry.getValue(), manifestId, manifestUrl, catalogueEntry.getKey());
  }

  public Catalogue mapCatalogue(
      spectacular.backend.cataloguemanifest.model.Catalogue catalogue,
      CatalogueId catalogueId,
      URI manifestUrl) {
    return mapCatalogue(catalogue, catalogueId, manifestUrl, catalogueId.getCatalogueName());
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
      String catalogueName) {
    return new Catalogue()
        .encodedId(manifestId.createCatalogueId(catalogueName).getCombined().getBytes())
        .fullPath(manifestId.getFullPath())
        .name(catalogueName)
        .title(catalogue.getTitle())
        .description(catalogue.getDescription())
        .htmlUrl(manifestUrl)
        .interfaceCount(catalogue.getInterfaces().getAdditionalProperties().size());
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
