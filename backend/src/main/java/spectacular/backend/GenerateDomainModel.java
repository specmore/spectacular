package spectacular.backend;

import ch.ifocusit.plantuml.classdiagram.ClassDiagramBuilder;
import spectacular.backend.catalogues.CatalogueController;
import spectacular.backend.catalogues.CatalogueService;
import spectacular.backend.catalogues.CataloguesResponse;
import spectacular.backend.common.Repository;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.app.AppApiClient;
import spectacular.backend.github.app.AppAuthenticationService;
import spectacular.backend.github.app.AppInstallationAccessTokenStore;
import spectacular.backend.github.app.AppInstallationAuthenticationHeaderRequestInterceptor;
import spectacular.backend.github.app.AppInstallationContextProvider;
import spectacular.backend.github.app.AppInstallationService;
import spectacular.backend.github.app.GitHubAppAuthenticationHeaderRequestInterceptor;
import spectacular.backend.github.domain.AccessTokenResult;

public class GenerateDomainModel {
  /**
   * A program to generate a class diagram.
   *
   * @param args any runtime arguments passed in
   */
  public static void main(String[] args) {
    String diagram = new ClassDiagramBuilder()
        .addClasse(
            CatalogueController.class,
            CatalogueService.class,
            CataloguesResponse.class,

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
