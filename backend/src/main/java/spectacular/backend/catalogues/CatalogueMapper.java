package spectacular.backend.catalogues;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spectacular.backend.api.model.Catalogue;
import spectacular.backend.common.Repository;

public class CatalogueMapper {
  private static final Logger logger = LoggerFactory.getLogger(CatalogueMapper.class);

  public static Catalogue mapCatalogueManifestEntry(
      Map.Entry<String, spectacular.backend.cataloguemanifest.model.Catalogue> catalogueEntry,
      CatalogueManifestId manifestId) {
    var catalogue = catalogueEntry.getValue();
    return new Catalogue()
        .repository(toApiModelRepository(manifestId.getRepository()))
        .filePath(manifestId.getPath())
        .name(catalogueEntry.getKey())
        .title(catalogue.getTitle())
        .description(catalogue.getDescription())
        .interfaceCount(catalogue.getInterfaces().getAdditionalProperties().size());
  }

  public static Catalogue createForParseError(String parseError, CatalogueManifestId manifestId) {
    return new Catalogue()
        .repository(toApiModelRepository(manifestId.getRepository()))
        .filePath(manifestId.getPath())
        .name("error")
        .parseError(parseError);
  }

  public static spectacular.backend.api.model.Repository toApiModelRepository(Repository repository) {
    URI htmlUri = null;
    try {
      htmlUri = new URI(repository.getHtmlUrl());
    } catch (URISyntaxException e) {
      logger.error("A repository htmlUrl was invalid URI syntax: " + repository.getHtmlUrl(), e);
    }
    return new spectacular.backend.api.model.Repository()
        .name(repository.getName())
        .owner(repository.getOwner())
        .nameWithOwner(repository.getNameWithOwner())
        .htmlUrl(htmlUri);
  }
}
