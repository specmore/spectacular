package spectacular.github.service.catalogues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

    public CatalogueController(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
    }

    @GetMapping("api/{org}/catalogues")
    public CataloguesResponse getCatalogues(@PathVariable("org") String orgName, JwtAuthenticationToken authToken) {
        var catalogues = this.catalogueService.getCataloguesForOrgAndUser(orgName, authToken.getName());
        return new CataloguesResponse(catalogues);
    }
}
