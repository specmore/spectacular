package spectacular.backend.github.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Blob {
  private final String text;

  public Blob(@JsonProperty("text") String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }
}
