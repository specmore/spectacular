package spectacular.github.service.catalogues;

import spectacular.github.service.common.Repository;

public class Catalogue {
    private final Repository repository;
    private final CatalogueManifest catalogueManifest;
    private final String error;

    public Catalogue(Repository repository, CatalogueManifest catalogueManifest, String error) {
        this.repository = repository;
        this.catalogueManifest = catalogueManifest;
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
}
