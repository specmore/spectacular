package spectacular.backend.github.refs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.domain.Tag;
import spectacular.backend.github.graphql.GraphQlRequest;
import spectacular.backend.github.pullrequests.PullRequest;

@Repository
public class RefRepository {
  private static final Logger logger = LoggerFactory.getLogger(RefRepository.class);
  private static final String branchQuery = "query { \n" +
      "  repository(owner: \"%s\", name:\"%s\") {\n" +
      "    nameWithOwner url\n" +
      "    refs(refPrefix:\"refs/heads/\", first:10, query:\"%s\") {\n" +
      "      totalCount\n" +
      "      nodes {\n" +
      "        name\n" +
      "        target {\n" +
      "            oid\n" +
      "            ... on Commit {\n" +
      "              message\n" +
      "              file(path:\"%s\") {\n" +
      "                name\n" +
      "                object {\n" +
      "                  ... on Blob {\n" +
      "                    text\n" +
      "                  }\n" +
      "                }\n" +
      "              }\n" +
      "            }\n" +
      "        }\n" +
      "        associatedPullRequests(first:100, states: [OPEN]) {\n" +
      "          totalCount\n" +
      "          nodes {\n" +
      "            number\n" +
      "            title\n" +
      "            url\n" +
      "            updatedAt\n" +
      "            headRef { name repository { nameWithOwner url } }\n" +
      "            baseRefName\n" +
      "            labels(first: 10) {\n" +
      "              totalCount\n" +
      "              nodes { name }\n" +
      "            }\n" +
      "            files(first: 100) {\n" +
      "              totalCount\n" +
      "              nodes { path }\n" +
      "            }\n" +
      "          }\n" +
      "        }\n" +
      "      }\n" +
      "    }\n" +
      "  }\n" +
      "}";

  private static final String tagsQuery = "query { \n" +
      "  repository(owner: \"%s\", name:\"%s\") {\n" +
      "    nameWithOwner url\n" +
      "    refs(refPrefix:\"refs/tags/\", first:10, query:\"%s\") {\n" +
      "      totalCount\n" +
      "      nodes {\n" +
      "        name\n" +
      "      }\n" +
      "    }\n" +
      "  }\n" +
      "}";

  private final RestApiClient restApiClient;

  public RefRepository(RestApiClient restApiClient) {
    this.restApiClient = restApiClient;
  }


  /**
   * Gets the contents of a specific file in all the branches of a Git Repo where the branch names matches a specific query.
   * @param repoId the repo to search
   * @param query the branch name query
   * @param specFilePath the path to the file contents
   * @return a list of BranchRef objects containing the file contents and associated pull requests
   */
  public List<BranchRef> getBranchesForRepo(RepositoryId repoId, String query, String specFilePath) {
    String formattedQuery = String.format(branchQuery, repoId.getOwner(), repoId.getName(), query, specFilePath);

    var response = restApiClient.graphQlQuery(new GraphQlRequest(formattedQuery));

    if (!response.getErrors().isEmpty()) {
      logger.error("The following error occurred while fetching pull requests for repo '" +
          repoId.getNameWithOwner() + "': " + response.getErrors().toString());
    } else {
      return response.getData().getRepository().getRefs().getNodes().stream()
          .map(BranchRef::createBranchRefFrom)
          .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }

  /**
   * Gets all the tags of a Git Repo where the tag names matches a specific query.
   * @param repoId the repo to search
   * @param query the tag name query
   * @return a list of Tag objects
   */
  public List<Tag> getTagsForRepo(RepositoryId repoId, String query) {
    String formattedQuery = String.format(tagsQuery, repoId.getOwner(), repoId.getName(), query);

    var response = restApiClient.graphQlQuery(new GraphQlRequest(formattedQuery));

    if (!response.getErrors().isEmpty()) {
      logger.error("The following error occurred while fetching pull requests for repo '" +
          repoId.getNameWithOwner() + "': " + response.getErrors().toString());
    } else {
      return response.getData().getRepository().getRefs().getNodes().stream()
          .map(ref -> new Tag(ref.getName()))
          .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }
}
