package spectacular.github.service.catalogues;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import spectacular.github.service.github.app.AppInstallationContextProvider;
import spectacular.github.service.common.Repository;

import java.io.IOException;

@RestController
public class CatalogueController {
    private final CatalogueService catalogueService;
    private final AppInstallationContextProvider appInstallationContextProvider;

    public CatalogueController(CatalogueService catalogueService, AppInstallationContextProvider appInstallationContextProvider) {
        this.catalogueService = catalogueService;
        this.appInstallationContextProvider = appInstallationContextProvider;
    }

    @GetMapping("api/{installationId}/{configOwner}/{configRepoName}/catalogues")
    public CataloguesResponse getCatalogues(@PathVariable("installationId") String installationId, @PathVariable("configOwner") String repoOwner, @PathVariable("configRepoName") String repoName, Authentication authentication) throws IOException {
        //todo: move installationId to a header and set context using a interceptor
        appInstallationContextProvider.setInstallationId(installationId);
        var configRepository = new Repository(repoOwner, repoName);

        var catalogueItems = this.catalogueService.getCatalogueItemsForAppConfig(configRepository);
        return new CataloguesResponse(catalogueItems);
    }
}
