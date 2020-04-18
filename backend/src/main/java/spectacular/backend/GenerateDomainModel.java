package spectacular.backend;

import ch.ifocusit.plantuml.classdiagram.ClassDiagramBuilder;
import spectacular.backend.catalogues.CatalogueController;
import spectacular.backend.catalogues.CatalogueService;
import spectacular.backend.catalogues.CataloguesResponse;
import spectacular.backend.common.Repository;
import spectacular.backend.config.instance.InstanceConfig;
import spectacular.backend.config.instance.InstanceConfigManifest;
import spectacular.backend.config.instance.InstanceConfigService;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.app.*;
import spectacular.backend.config.instance.Catalogue;
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
