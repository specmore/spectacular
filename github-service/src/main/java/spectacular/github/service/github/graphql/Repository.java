package spectacular.github.service.github.graphql;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Repository {
    private final String nameWithOwner;
    private final String url;
    private final Connection<PullRequest> pullRequests;

    public Repository(@JsonProperty("nameWithOwner") String nameWithOwner, @JsonProperty("url") String url, @JsonProperty("pullRequests") Connection<PullRequest> pullRequests) {
        this.nameWithOwner = nameWithOwner;
        this.url = url;
        this.pullRequests = pullRequests;
    }

    public String getUrl() {
        return url;
    }

    public String getNameWithOwner() {
        return nameWithOwner;
    }

    public Connection<PullRequest> getPullRequests() {
        return pullRequests;
    }
}
