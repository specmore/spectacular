package spectacular.github.service.catalogues;

import java.util.List;

public class CatalogueService {
    public List<CatalogueItem> getCatalogueItemsForAppConfig() {
        return List.of(new CatalogueItem("Test Catalogue 1", "pburls/specs-test"));
    }
}
