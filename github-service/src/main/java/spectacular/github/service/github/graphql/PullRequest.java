package spectacular.github.service.github.graphql;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PullRequest {
    private final int number;
    private final String url;
    private final Connection<Label> labels;
    private final Connection<ChangedFile> changedFiles;
    private final String title;
    private final Ref headRef;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PullRequest(@JsonProperty("number") int number,
                       @JsonProperty("url") String url,
                       @JsonProperty("labels") Connection<Label> labels,
                       @JsonProperty("files") Connection<ChangedFile> changedFiles,
                       @JsonProperty("title") String title,
                       @JsonProperty("headRef") Ref headRef) {
        this.number = number;
        this.url = url;
        this.labels = labels;
        this.changedFiles = changedFiles;
        this.title = title;
        this.headRef = headRef;
    }

    public int getNumber() {
        return number;
    }

    public String getUrl() {
        return url;
    }

    public Connection<Label> getLabels() {
        return labels;
    }

    public Connection<ChangedFile> getChangedFiles() {
        return changedFiles;
    }

    public String getTitle() { return title; }

    public Ref getHeadRef() {
        return headRef;
    }
}

