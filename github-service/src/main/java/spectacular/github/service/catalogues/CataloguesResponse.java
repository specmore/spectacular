package spectacular.github.service.catalogues;

import java.util.List;

public class CataloguesResponse {
    private final List<CatalogueItem> catalogueItems;

    public CataloguesResponse(List<CatalogueItem> catalogueItems) {
        this.catalogueItems = catalogueItems;
    }

    public List<CatalogueItem> getCatalogueItems() {
        return catalogueItems;
    }
}
