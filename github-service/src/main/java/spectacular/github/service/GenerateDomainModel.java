package spectacular.github.service;

import ch.ifocusit.plantuml.classdiagram.ClassDiagramBuilder;
import spectacular.github.service.catalogues.CatalogueController;
import spectacular.github.service.catalogues.CatalogueService;
import spectacular.github.service.catalogues.CataloguesResponse;
import spectacular.github.service.common.Repository;
import spectacular.github.service.config.instance.Catalogue;
import spectacular.github.service.config.instance.InstanceConfig;
import spectacular.github.service.config.instance.InstanceConfigManifest;
import spectacular.github.service.config.instance.InstanceConfigService;
import spectacular.github.service.github.RestApiClient;
import spectacular.github.service.github.app.*;

public class GenerateDomainModel {
    public static void main (String args[]) {
        String diagram = new ClassDiagramBuilder()
                .addClasse(
                        CatalogueController.class,
                        CatalogueService.class,
                        CataloguesResponse.class,

                        Catalogue.class,
                        InstanceConfig.class,
                        InstanceConfigManifest.class,
                        InstanceConfigService.class,

                        AccessTokenResult.class,
                        AppApiClient.class,
                        AppAuthenticationService.class,
                        AppInstallationAccessTokenStore.class,
                        AppInstallationAuthenticationHeaderRequestInterceptor.class,
                        AppInstallationContextProvider.class,
                        AppInstallationService.class,
                        GitHubAppAuthenticationHeaderRequestInterceptor.class,
                        Repository.class,
                        RestApiClient.class
                        )
                .build();
        System.out.println(diagram);
    }
}
