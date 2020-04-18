package spectacular.backend.pullrequests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import spectacular.backend.common.Repository;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.graphql.GraphQLRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PullRequestService {
    private final static Logger logger = LoggerFactory.getLogger(PullRequestService.class);

    private final static String PullRequestsGraphQLQuery = "query {\n" +
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

    public PullRequestService(RestApiClient restApiClient) {
        this.restApiClient = restApiClient;
    }

    public List<PullRequest> getPullRequestsForRepo(Repository repo) {
        String formattedQuery = String.format(PullRequestsGraphQLQuery, repo.getOwner(), repo.getName());

        var response = restApiClient.graphQLQuery(new GraphQLRequest(formattedQuery));

        if (!response.getErrors().isEmpty()) {
            logger.error("The following error occurred while fetching pull requests for repo '" + repo.getNameWithOwner() + "': " + response.getErrors().toString());
            return new ArrayList<>();
        }

        return response.getData().getRepository().getPullRequests().getNodes().stream().map(PullRequest::createPullRequestFrom).collect(Collectors.toList());
    }
}
