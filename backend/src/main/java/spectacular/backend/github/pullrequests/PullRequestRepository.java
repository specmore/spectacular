package spectacular.backend.github.pullrequests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.graphql.GraphQlRequest;

public class PullRequestRepository {
  private static final Logger logger = LoggerFactory.getLogger(PullRequestRepository.class);

  private static final String PullRequestsGraphQLQuery = "query {\n" +
      "    repository(owner: \"%s\", name:\"%s\") {\n" +
      "        nameWithOwner\n" +
      "        url\n" +
      "        pullRequests(first: 100, baseRefName: \"%s\", states: [OPEN]) {\n" +
      "            totalCount\n" +
      "            nodes {\n" +
      "                number\n" +
      "                url\n" +
      "                updatedAt\n" +
      "                headRef { name repository { nameWithOwner url } }\n" +
      "                baseRefName\n" +
      "                labels(first: 100) {\n" +
      "                    totalCount\n" +
      "                    nodes { name }\n" +
      "                }\n" +
      "                files(first:100) {\n" +
      "                    totalCount\n" +
      "                    nodes { path }\n" +
      "                }\n" +
      "                title\n" +
      "            }\n" +
      "        }\n" +
      "    }\n" +
      "}";

  private final RestApiClient restApiClient;
  private final Map<CacheKey, List<PullRequest>> cache;

  public PullRequestRepository(RestApiClient restApiClient) {
    this.restApiClient = restApiClient;
    cache = new HashMap<>();
  }

  /**
   * Gets all the open Pull Requests for a specific repository.
   *
   * @param repoId the repository to get Pull Requests for
   * @param baseBranchName the branch name the Pull Requests are targeting to merge into
   * @return a list of open PullRequests
   */
  public List<PullRequest> getPullRequestsForRepo(RepositoryId repoId, String baseBranchName) {
    var key = new CacheKey(repoId, baseBranchName);
    var cachedPullRequests = cache.get(key);
    if (cachedPullRequests == null) {
      String formattedQuery = String.format(PullRequestsGraphQLQuery, repoId.getOwner(), repoId.getName(), baseBranchName);

      var response = restApiClient.graphQlQuery(new GraphQlRequest(formattedQuery));

      if (!response.getErrors().isEmpty()) {
        logger.error("The following error occurred while fetching pull requests for repo '" +
            repoId.getNameWithOwner() + "': " + response.getErrors().toString());
        cachedPullRequests = new ArrayList<>();
      } else {
        cachedPullRequests = response.getData().getRepository().getPullRequests().getNodes().stream()
            .filter(pullRequest -> pullRequest.getHeadRef() != null)
            .map(PullRequest::createPullRequestFrom)
            .collect(Collectors.toList());
      }
      cache.put(key, cachedPullRequests);
    }
    return cachedPullRequests;
  }

  /**
   * Gets all the open Pull Requests for a specific repository and target branch that have changed a specific file.
   *
   * @param repoId the id of the Repository to get open Pull Requests for
   * @param filePath the file path of the file that has changed in the Pull Requests
   * @param baseBranchName the branch name the Pull Requests are targeting to merge into
   * @return a List of PullRequest
   */
  public List<PullRequest> getPullRequestsForRepoAndFile(RepositoryId repoId, String filePath, String baseBranchName) {
    var openPullRequests = getPullRequestsForRepo(repoId, baseBranchName);
    return openPullRequests.stream()
        .filter(pullRequest -> pullRequest.changesFile(repoId, filePath))
        .collect(Collectors.toList());
  }

  private class CacheKey {
    private final RepositoryId repositoryId;
    private final String branchName;

    private CacheKey(RepositoryId repositoryId, String branchName) {
      this.repositoryId = repositoryId;
      this.branchName = branchName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      CacheKey cacheKey = (CacheKey) o;
      return repositoryId.equals(cacheKey.repositoryId) &&
          branchName.equals(cacheKey.branchName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(repositoryId, branchName);
    }
  }
}
