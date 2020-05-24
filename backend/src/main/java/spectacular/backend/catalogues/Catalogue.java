package spectacular.backend.catalogues;

import java.util.List;
import javax.validation.constraints.NotNull;
import spectacular.backend.common.Repository;
import spectacular.backend.specs.SpecLog;

public class Catalogue {
  private final CatalogueManifestId id;
  private final CatalogueManifest catalogueManifest;
  private final List<SpecLog> specLogs;
  private final String error;

  private Catalogue(CatalogueManifestId id, CatalogueManifest catalogueManifest,
                    List<SpecLog> specLogs, String error) {
    this.id = id;
    this.catalogueManifest = catalogueManifest;
    this.specLogs = specLogs;
    this.error = error;
  }

  public static Catalogue create(@NotNull Repository repository,
                                 @NotNull String path,
                                 CatalogueManifest catalogueManifest,
                                 String error) {
    return new Catalogue(new CatalogueManifestId(repository, path), catalogueManifest, null, error);
  }

  public static Catalogue create(@NotNull CatalogueManifestId catalogueManifestId,
                                 CatalogueManifest catalogueManifest,
                                 String error) {
    return new Catalogue(catalogueManifestId, catalogueManifest, null, error);
  }

  public CatalogueManifestId getId() {
    return id;
  }

  public CatalogueManifest getCatalogueManifest() {
    return catalogueManifest;
  }

  public String getError() {
    return error;
  }

  public List<SpecLog> getSpecLogs() {
    return specLogs;
  }

  public Catalogue with(List<SpecLog> specLogs) {
    return new Catalogue(this.id, this.catalogueManifest, specLogs, this.error);
  }
}
