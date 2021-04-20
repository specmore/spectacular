package spectacular.backend.github.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Commit {
  private final String oid;
  private final String message;
  private final File file;

  /**
   * A GitHub GraphQL Commit object.
   * @param oid the git object id
   * @param message the commit message
   * @param file a file tree entry for a specified path
   */
  public Commit(@JsonProperty("oid") String oid,
                @JsonProperty("message") String message,
                @JsonProperty("file") File file) {
    this.oid = oid;
    this.message = message;
    this.file = file;
  }

  public String getOid() {
    return oid;
  }

  public String getMessage() {
    return message;
  }

  public File getFile() {
    return file;
  }
}
