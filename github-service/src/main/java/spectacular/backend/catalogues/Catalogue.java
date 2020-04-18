package spectacular.backend.catalogues;

import spectacular.backend.common.Repository;
import spectacular.backend.specs.SpecLog;

import java.util.List;

public class Catalogue {
    private final Repository repository;
    private final CatalogueManifest catalogueManifest;
    private final List<SpecLog> specLogs;
    private final String error;

    private Catalogue(Repository repository, CatalogueManifest catalogueManifest, List<SpecLog> specLogs, String error) {
        this.repository = repository;
        this.catalogueManifest = catalogueManifest;
        this.specLogs = specLogs;
        this.error = error;
    }

    public Repository getRepository() {
        return repository;
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

    public static Catalogue create(Repository repository, CatalogueManifest catalogueManifest, String error) {
        return new Catalogue(repository, catalogueManifest, null, error);
    }

    public Catalogue with(List<SpecLog> specLogs) {
        return new Catalogue(this.repository, this.catalogueManifest, specLogs, this.error);
    }
}
