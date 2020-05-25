package spectacular.backend.api.mapper;

import spectacular.backend.api.model.PullRequest;

public class PullRequestMapper {
  /**
   * Maps a PullRequest item to an API PullRequest model.
   *
   * @param pullRequest the PullRequest to be mapped
   * @return a PullRequest API model
   */
  public static PullRequest mapGitHubPullRequest(spectacular.backend.pullrequests.PullRequest pullRequest) {
    return new PullRequest()
        .number(pullRequest.getNumber())
        .title(pullRequest.getTitle())
        .url(pullRequest.getUrl())
        .labels(pullRequest.getLabels())
        .updatedAt(pullRequest.getUpdatedAt());
  }
}
