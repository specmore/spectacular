package spectacular.github.service.catalogues;

import java.util.List;

public class CataloguesResponse {
    private final List<Catalogue> catalogues;

    public CataloguesResponse(List<Catalogue> catalogues) {
        this.catalogues = catalogues;
    }

    public List<Catalogue> getCatalogues() {
        return catalogues;
    }
}
