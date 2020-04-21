package spectacular.backend.pullrequests;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import spectacular.backend.common.Repository;

public class PullRequest {
  private final Repository repository;
  private final String branchName;
  private final int number;
  private final String url;
  private final List<String> labels;
  private final List<String> changedFiles;
  private final String title;
  private final Instant updatedAt;

  public PullRequest(Repository repository, String branchName, int number, String url,
                     List<String> labels, List<String> changedFiles, String title,
                     Instant updatedAt) {
    this.repository = repository;
    this.branchName = branchName;
    this.number = number;
    this.url = url;
    this.labels = labels;
    this.changedFiles = changedFiles;
    this.title = title;
    this.updatedAt = updatedAt;
  }

  public static PullRequest createPullRequestFrom(
      spectacular.backend.github.graphql.PullRequest pullRequest) {
    var repository = Repository.createRepositoryFrom(pullRequest.getHeadRef().getRepository());
    var branchName = pullRequest.getHeadRef().getName();
    List<String> labels = pullRequest.getLabels().getNodes().stream().map(label -> label.getName())
        .collect(Collectors.toList());
    List<String> changedFiles =
        pullRequest.getChangedFiles().getNodes().stream().map(file -> file.getPath())
            .collect(Collectors.toList());
    return new PullRequest(repository, branchName, pullRequest.getNumber(), pullRequest.getUrl(),
        labels, changedFiles, pullRequest.getTitle(), pullRequest.getUpdatedAt());
  }

  public boolean changesFile(Repository repo, String filePath) {
    return repo.equals(this.repository) && changedFiles.contains(filePath);
  }

  public String getBranchName() {
    return branchName;
  }

  public int getNumber() {
    return number;
  }

  public String getUrl() {
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

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
