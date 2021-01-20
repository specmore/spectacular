package spectacular.backend;

import ch.ifocusit.plantuml.classdiagram.ClassDiagramBuilder;
import spectacular.backend.github.pullrequests.PullRequest;

public class GenerateDomainModel {
  /**
   * A program to generate a class diagram.
   *
   * @param args any runtime arguments passed in
   */
  public static void main(String[] args) {
    String diagram = new ClassDiagramBuilder()
        .addClasse(
            PullRequest.class
        )
        .build();
    System.out.println(diagram);
  }
}
