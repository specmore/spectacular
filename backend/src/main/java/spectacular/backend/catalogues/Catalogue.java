package spectacular.backend.catalogues;

import spectacular.backend.common.Repository;
import spectacular.backend.specs.SpecLog;

import javax.validation.constraints.NotNull;
import java.util.List;

public class Catalogue {
    private final String id;
    private final Repository repository;
    private final CatalogueManifest catalogueManifest;
    private final List<SpecLog> specLogs;
    private final String error;

    private Catalogue(String id, Repository repository, CatalogueManifest catalogueManifest, List<SpecLog> specLogs, String error) {
        this.id = id;
        this.repository = repository;
        this.catalogueManifest = catalogueManifest;
        this.specLogs = specLogs;
        this.error = error;
    }

    public String getId() {
        return id;
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

    public static Catalogue create(@NotNull Repository repository, CatalogueManifest catalogueManifest, String error) {
        return new Catalogue(repository.getNameWithOwner(), repository, catalogueManifest, null, error);
    }

    public Catalogue with(List<SpecLog> specLogs) {
        return new Catalogue(this.id, this.repository, this.catalogueManifest, specLogs, this.error);
    }
}
