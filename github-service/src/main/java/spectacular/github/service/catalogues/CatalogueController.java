package spectacular.github.service.catalogues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import spectacular.github.service.github.app.AppAuthenticationService;
import spectacular.github.service.github.app.AppInstallationContextProvider;
import spectacular.github.service.common.Repository;

import java.io.IOException;

@RestController
public class CatalogueController {
    private static final Logger logger = LoggerFactory.getLogger(CatalogueController.class);
    private final CatalogueService catalogueService;
    private final AppInstallationContextProvider appInstallationContextProvider;

    public CatalogueController(CatalogueService catalogueService, AppInstallationContextProvider appInstallationContextProvider) {
        this.catalogueService = catalogueService;
        this.appInstallationContextProvider = appInstallationContextProvider;
    }

    @GetMapping("api/catalogues")
    public CataloguesResponse getCatalogues() {
        return null;
    }

    @GetMapping("api/{installationId}/{configOwner}/{configRepoName}/catalogues")
    public CataloguesResponse getCatalogues(@PathVariable("installationId") String installationId, @PathVariable("configOwner") String repoOwner, @PathVariable("configRepoName") String repoName, Authentication authentication) throws IOException {
        var installationIdHeaderValue = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest().getHeader("x-spec-installation-id");
        logger.info("installationIdHeaderValue: " + installationIdHeaderValue);
        //todo: move installationId to a header and set context using a interceptor
        appInstallationContextProvider.setInstallationId(installationId);
        var configRepository = new Repository(repoOwner, repoName);

        var catalogueItems = this.catalogueService.getCatalogueItemsForAppConfig(configRepository);
        return new CataloguesResponse(catalogueItems);
    }
}
