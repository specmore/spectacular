package spectacular.backend.specs;

import java.time.Instant;
import javax.validation.constraints.NotNull;
import spectacular.backend.api.model.OpenApiSpecParseResult;
import spectacular.backend.common.RepositoryId;

public class SpecItem {
  private final String id;
  private final RepositoryId repositoryId;
  private final String filePath;
  private final String htmlUrl;
  private final String ref;
  private final String sha;
  private final Instant lastModified;
  private final OpenApiSpecParseResult parseResult;

  /**
   * Constructs a SpecItem object representing a snapshot of a specific spec file at a point in the git repository's commit history and
   * the parsed contents of the OpenAPI YAML file.
   *
   * @param repositoryId the repository the spec file belongs to
   * @param filePath the path of the spec file
   * @param htmlUrl the url of the spec file
   * @param ref a reference to the commit at which the snapshot is taken
   * @param sha the SHA identifier of the commit at which the snapshot is taken
   * @param lastModified the date and time when the commit was made
   * @param parseResult the OpenApiSpecParseResult of the Spec File's contents
   */
  public SpecItem(@NotNull RepositoryId repositoryId, @NotNull String filePath, String htmlUrl,
                  String ref, String sha, Instant lastModified,
                  OpenApiSpecParseResult parseResult) {
    this.id = repositoryId.getNameWithOwner() + "/" + ref + "/" + filePath;
    this.repositoryId = repositoryId;
    this.filePath = filePath;
    this.htmlUrl = htmlUrl;
    this.ref = ref;
    this.sha = sha;
    this.lastModified = lastModified;
    this.parseResult = parseResult;
  }

  public String getId() {
    return id;
  }

  public RepositoryId getRepositoryId() {
    return repositoryId;
  }

  public String getFilePath() {
    return filePath;
  }

  public OpenApiSpecParseResult getParseResult() {
    return parseResult;
  }

  public String getHtmlUrl() {
    return htmlUrl;
  }

  public String getRef() {
    return ref;
  }

  public String getSha() {
    return sha;
  }

  public Instant getLastModified() {
    return lastModified;
  }
}
