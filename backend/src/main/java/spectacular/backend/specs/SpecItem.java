package spectacular.backend.specs;

import java.time.Instant;
import javax.validation.constraints.NotNull;
import spectacular.backend.common.Repository;
import spectacular.backend.specs.openapi.OpenApiSpecParseResult;

public class SpecItem {
  private final String id;
  private final Repository repository;
  private final String filePath;
  private final String htmlUrl;
  private final String ref;
  private final String sha;
  private final Instant lastModified;
  private final OpenApiSpecParseResult parseResult;

  public SpecItem(@NotNull Repository repository, @NotNull String filePath, String htmlUrl,
                  String ref, String sha, Instant lastModified,
                  OpenApiSpecParseResult parseResult) {
    this.id = repository.getNameWithOwner() + "/" + ref + "/" + filePath;
    this.repository = repository;
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

  public Repository getRepository() {
    return repository;
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
