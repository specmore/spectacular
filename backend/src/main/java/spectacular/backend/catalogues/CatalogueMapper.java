package spectacular.backend.catalogues;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spectacular.backend.api.model.Catalogue;
import spectacular.backend.cataloguemanifest.model.CatalogueManifest;
import spectacular.backend.cataloguemanifest.model.SpecFileLocation;

public class CatalogueMapper {
  private static final Logger logger = LoggerFactory.getLogger(CatalogueMapper.class);

  public static List<Catalogue> mapCatalogueManifestEntries(CatalogueManifest catalogueManifest, CatalogueManifestId manifestId) {
    return catalogueManifest.getCatalogues().getAdditionalProperties().entrySet().stream()
        .map(catalogueEntry -> CatalogueMapper.mapCatalogueManifestEntry(catalogueEntry, manifestId)).collect(Collectors.toList());
  }

  public static Catalogue mapCatalogueManifestEntry(
      Map.Entry<String, spectacular.backend.cataloguemanifest.model.Catalogue> catalogueEntry,
      CatalogueManifestId manifestId) {
    return mapCatalogue(catalogueEntry.getValue(), manifestId, catalogueEntry.getKey());
  }

  public static Catalogue mapCatalogue(
      spectacular.backend.cataloguemanifest.model.Catalogue catalogue,
      CatalogueId catalogueId) {
    return mapCatalogue(catalogue, catalogueId, catalogueId.getCatalogueName());
  }

  /**
   * Maps a manifest catalogue item to an API Catalogue model.
   *
   * @param catalogue the manifest catalogue item
   * @param manifestId the id of the manifest
   * @param catalogueName the name of the catalogue item
   * @return a Catalogue API model
   */
  public static Catalogue mapCatalogue(
      spectacular.backend.cataloguemanifest.model.Catalogue catalogue,
      CatalogueManifestId manifestId,
      String catalogueName) {
    return new Catalogue()
        .fullPath(manifestId.getFullPath())
        .name(catalogueName)
        .title(catalogue.getTitle())
        .description(catalogue.getDescription())
        .interfaceCount(catalogue.getInterfaces().getAdditionalProperties().size());
  }

  /**
   * Creates an API Catalogue model that represents a fail parsing of an entire Catalogue Manifest file.
   *
   * @param parseError the error that occurred while parsing the manifest file
   * @param manifestId the id of the manifest file being parsed
   * @return a Catalogue API model
   */
  public static Catalogue createForParseError(String parseError, CatalogueManifestId manifestId) {
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
  public static Catalogue createForParseError(String parseError, CatalogueId catalogueId) {
    return new Catalogue()
        .fullPath(catalogueId.getFullPath())
        .name(catalogueId.getCatalogueName())
        .parseError(parseError);
  }
}
