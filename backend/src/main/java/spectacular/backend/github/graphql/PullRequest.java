package spectacular.backend.github.graphql;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.OffsetDateTime;

public class PullRequest {
  private final int number;
  private final URI url;
  private final Connection<Label> labels;
  private final Connection<ChangedFile> changedFiles;
  private final String title;
  private final OffsetDateTime updatedAt;
  private final Ref headRef;

  /**
   * Constructs a PullRequest from the response of a GitHub GraphQL PullRequest object result.
   *
   * @param number the PR number
   * @param url the URL to the PR
   * @param labels a list of labels associated to the PR
   * @param changedFiles a list of files changed in the PR
   * @param title the title of the PR
   * @param updatedAt when the PR was last updated
   * @param headRef the branch the PR has been opened from
   */
  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public PullRequest(@JsonProperty("number") int number,
                     @JsonProperty("url") URI url,
                     @JsonProperty("labels") Connection<Label> labels,
                     @JsonProperty("files") Connection<ChangedFile> changedFiles,
                     @JsonProperty("title") String title,
                     @JsonProperty("updatedAt") OffsetDateTime updatedAt,
                     @JsonProperty("headRef") Ref headRef) {
    this.number = number;
    this.url = url;
    this.labels = labels;
    this.changedFiles = changedFiles;
    this.title = title;
    this.updatedAt = updatedAt;
    this.headRef = headRef;
  }

  public int getNumber() {
    return number;
  }

  public URI getUrl() {
    return url;
  }

  public Connection<Label> getLabels() {
    return labels;
  }

  public Connection<ChangedFile> getChangedFiles() {
    return changedFiles;
  }

  public String getTitle() {
    return title;
  }

  public Ref getHeadRef() {
    return headRef;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }
}

