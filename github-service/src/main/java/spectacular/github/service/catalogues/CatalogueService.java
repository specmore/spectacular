package spectacular.github.service.catalogues;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogueService {
    public List<CatalogueItem> getCatalogueItemsForAppConfig() {
        return List.of(new CatalogueItem("Test Catalogue 1", "pburls/specs-test"));
    }
}
