package spectacular.github.service.catalogues;

import org.springframework.stereotype.Service;
import spectacular.github.service.config.instance.Catalogue;
import spectacular.github.service.config.instance.InstanceConfigService;
import spectacular.github.service.common.Repository;

import java.io.IOException;
import java.util.List;

@Service
public class CatalogueService {
    private final InstanceConfigService instanceConfigService;

    public CatalogueService(InstanceConfigService instanceConfigService) {
        this.instanceConfigService = instanceConfigService;
    }

    public List<Catalogue> getCatalogueItemsForAppConfig(Repository configRepository) throws IOException {
        var instanceConfig = instanceConfigService.getInstanceConfigForRepository(configRepository);
        return instanceConfig.getInstanceConfigManifest().getCatalogues();
    }
}
