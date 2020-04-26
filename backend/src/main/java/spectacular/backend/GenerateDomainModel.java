package spectacular.backend;

import ch.ifocusit.plantuml.classdiagram.ClassDiagramBuilder;
import spectacular.backend.catalogues.Catalogue;
import spectacular.backend.catalogues.CatalogueManifest;
import spectacular.backend.catalogues.SpecFileLocation;
import spectacular.backend.common.Repository;
import spectacular.backend.installation.InstallationResponse;
import spectacular.backend.pullrequests.PullRequest;
import spectacular.backend.specs.ProposedSpecChange;
import spectacular.backend.specs.SpecItem;
import spectacular.backend.specs.SpecLog;
import spectacular.backend.specs.openapi.OpenApiOperation;
import spectacular.backend.specs.openapi.OpenApiSpec;
import spectacular.backend.specs.openapi.OpenApiSpecParseResult;

public class GenerateDomainModel {
  /**
   * A program to generate a class diagram.
   *
   * @param args any runtime arguments passed in
   */
  public static void main(String[] args) {
    String diagram = new ClassDiagramBuilder()
        .addClasse(
            InstallationResponse.class,
            Repository.class,
            Catalogue.class,
            SpecFileLocation.class,
            CatalogueManifest.class,
            SpecLog.class,
            SpecItem.class,
            ProposedSpecChange.class,
            OpenApiSpec.class,
            OpenApiOperation.class,
            OpenApiSpecParseResult.class,
            PullRequest.class
        )
        .build();
    System.out.println(diagram);
  }
}
