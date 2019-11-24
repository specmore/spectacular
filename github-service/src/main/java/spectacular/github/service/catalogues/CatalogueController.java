package spectacular.github.service.catalogues;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CatalogueController {
    private final CatalogueService catalogueService;

    public CatalogueController(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
    }

    @GetMapping("api/catalogues")
    public CataloguesResponse getCatalogues() {
        var catalogueItems = this.catalogueService.getCatalogueItemsForAppConfig();
        return new CataloguesResponse(catalogueItems);
    }
}
