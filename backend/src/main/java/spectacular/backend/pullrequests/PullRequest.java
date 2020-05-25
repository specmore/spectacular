package spectacular.backend.pullrequests;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import spectacular.backend.common.Repository;
import spectacular.backend.github.graphql.ChangedFile;
import spectacular.backend.github.graphql.Label;

public class PullRequest {
  private final Repository repository;
  private final String branchName;
  private final int number;
  private final URI url;
  private final List<String> labels;
  private final List<String> changedFiles;
  private final String title;
  private final OffsetDateTime updatedAt;

  /**
   * Constructs a PullRequest object representing a PullRequest in the git source control system.
   *
   * @param repository the repository the pull request belongs to
   * @param branchName the name of the branch the pull request is pull changes from
   * @param number the number of the PR
   * @param url the url of the PR
   * @param labels a list of labels associated to the PR
   * @param changedFiles a list of file paths representing the changed files in the PR
   * @param title the title of the PR
   * @param updatedAt the last time the PR was updated
   */
  public PullRequest(Repository repository, String branchName, int number, URI url,
                     List<String> labels, List<String> changedFiles, String title,
                     OffsetDateTime updatedAt) {
    this.repository = repository;
    this.branchName = branchName;
    this.number = number;
    this.url = url;
    this.labels = labels;
    this.changedFiles = changedFiles;
    this.title = title;
    this.updatedAt = updatedAt;
  }

  /**
   * Creates a PullRequest object from a GitHub GraphQL PullRequest response object.
   *
   * @param pullRequest the GitHub GraphQL PullRequest response object
   * @return a PullRequest object
   */
  public static PullRequest createPullRequestFrom(spectacular.backend.github.graphql.PullRequest pullRequest) {
    var repository = Repository.createRepositoryFrom(pullRequest.getHeadRef().getRepository());
    var branchName = pullRequest.getHeadRef().getName();
    List<String> labels = pullRequest.getLabels().getNodes().stream().map(Label::getName).collect(Collectors.toList());
    List<String> changedFiles = pullRequest.getChangedFiles().getNodes().stream().map(ChangedFile::getPath).collect(Collectors.toList());

    return new PullRequest(repository, branchName, pullRequest.getNumber(), pullRequest.getUrl(), labels, changedFiles,
        pullRequest.getTitle(), pullRequest.getUpdatedAt());
  }

  public boolean changesFile(spectacular.backend.common.Repository repoId, String filePath) {
    return repoId.getNameWithOwner().equals(this.repository.getNameWithOwner()) && changedFiles.contains(filePath);
  }

  public String getBranchName() {
    return branchName;
  }

  public int getNumber() {
    return number;
  }

  public URI getUrl() {
    return url;
  }

  public List<String> getLabels() {
    return labels;
  }

  public List<String> getChangedFiles() {
    return changedFiles;
  }

  public String getTitle() {
    return title;
  }

  public Repository getRepository() {
    return repository;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }
}
