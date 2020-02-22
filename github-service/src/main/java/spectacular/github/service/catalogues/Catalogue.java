package spectacular.github.service.catalogues;

import spectacular.github.service.common.Repository;
import spectacular.github.service.specs.SpecEvolution;
import spectacular.github.service.specs.SpecItem;

import java.util.List;

public class Catalogue {
    private final Repository repository;
    private final CatalogueManifest catalogueManifest;
    private final List<SpecEvolution> specEvolutions;
    private final String error;

    public Catalogue(Repository repository, CatalogueManifest catalogueManifest, List<SpecEvolution> specEvolutions, String error) {
        this.repository = repository;
        this.catalogueManifest = catalogueManifest;
        this.specEvolutions = specEvolutions;
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

    public List<SpecEvolution> getSpecEvolutions() {
        return specEvolutions;
    }
}
