package spectacular.github.service.catalogues;

import spectacular.github.service.common.Repository;
import spectacular.github.service.specs.SpecLog;

import java.util.List;

public class Catalogue {
    private final Repository repository;
    private final CatalogueManifest catalogueManifest;
    private final List<SpecLog> specLogs;
    private final String error;

    public Catalogue(Repository repository, CatalogueManifest catalogueManifest, List<SpecLog> specLogs, String error) {
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
}
