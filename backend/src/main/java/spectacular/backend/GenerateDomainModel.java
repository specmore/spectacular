package spectacular.backend;

import ch.ifocusit.plantuml.classdiagram.ClassDiagramBuilder;
import spectacular.backend.github.pullrequests.PullRequest;
import spectacular.backend.installation.InstallationResponse;

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
            PullRequest.class
        )
        .build();
    System.out.println(diagram);
  }
}
