package spectacular.backend.pullrequests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import spectacular.backend.common.Repository;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.graphql.GraphQlRequest;

@Service
public class PullRequestService {
  private static final Logger logger = LoggerFactory.getLogger(PullRequestService.class);

  private static final String PullRequestsGraphQLQuery = "query {\n" +
      "    repository(owner: \"%s\", name:\"%s\") {\n" +
      "        nameWithOwner\n" +
      "        url\n" +
      "        pullRequests(first: 100, baseRefName: \"master\", states: [OPEN]) {\n" +
      "            totalCount\n" +
      "            nodes {\n" +
      "                number\n" +
      "                url\n" +
      "                updatedAt\n" +
      "                headRef { name repository { nameWithOwner url } }\n" +
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
  private final Map<Repository, List<PullRequest>> cache;

  public PullRequestService(RestApiClient restApiClient) {
    this.restApiClient = restApiClient;
    cache = new HashMap<>();
  }

  /**
   * Gets all the open Pull Requests for a specific repository.
   *
   * @param repoId the repository to get Pull Requests for
   * @return a list of open PullRequests
   */
  public List<PullRequest> getPullRequestsForRepo(Repository repoId) {
    var cachedPullRequest = cache.get(repoId);
    if (cachedPullRequest == null) {
      String formattedQuery = String.format(PullRequestsGraphQLQuery, repoId.getOwner(), repoId.getName());

      var response = restApiClient.graphQlQuery(new GraphQlRequest(formattedQuery));

      if (!response.getErrors().isEmpty()) {
        logger.error("The following error occurred while fetching pull requests for repo '" +
            repoId.getNameWithOwner() + "': " + response.getErrors().toString());
        cachedPullRequest = new ArrayList<>();
      } else {
        cachedPullRequest = response.getData().getRepository().getPullRequests().getNodes().stream()
            .filter(pullRequest -> pullRequest.getHeadRef() != null)
            .map(PullRequest::createPullRequestFrom)
            .collect(Collectors.toList());
      }
      cache.put(repoId, cachedPullRequest);
    }
    return cachedPullRequest;
  }

  public List<PullRequest> getPullRequestsForRepoAndFile(Repository repoId, String filePath) {
    var openPullRequests = getPullRequestsForRepo(repoId);
    return openPullRequests.stream()
        .filter(pullRequest -> pullRequest.changesFile(repoId, filePath))
        .collect(Collectors.toList());
  }
}
