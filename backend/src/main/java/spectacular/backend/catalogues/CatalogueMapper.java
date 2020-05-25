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

  public static Catalogue createForParseError(String parseError, CatalogueManifestId manifestId) {
    return new Catalogue()
        .fullPath(manifestId.getFullPath())
        .name("error")
        .parseError(parseError);
  }

  public static Catalogue createForParseError(String parseError, CatalogueId catalogueId) {
    return new Catalogue()
        .fullPath(catalogueId.getFullPath())
        .name(catalogueId.getCatalogueName())
        .parseError(parseError);
  }

  public static List<SpecFileLocation> getSpecFileLocationsWithRepos(
      spectacular.backend.cataloguemanifest.model.Catalogue catalogue,
      CatalogueManifestId manifestId
      ) {
    return catalogue.getInterfaces().getAdditionalProperties().entrySet().stream()
        .map(interfaceEntry -> {
          var specRepo = interfaceEntry.getValue().getSpecFile().getRepo() != null ? interfaceEntry.getValue().getSpecFile().getRepo() : manifestId.getRepository().getNameWithOwner();
          return new SpecFileLocation().withRepo(specRepo).withFilePath(interfaceEntry.getValue().getSpecFile().getFilePath());
        })
        .collect(Collectors.toList());
  }
}
