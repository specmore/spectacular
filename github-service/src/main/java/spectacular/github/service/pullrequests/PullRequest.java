package spectacular.github.service.pullrequests;

import spectacular.github.service.common.Repository;

import java.util.List;

public class PullRequest {
    private final Repository repository;
    private final String branchName;
    private final int number;
    private final String url;
    private final List<String> labels;
    private final List<String> changedFiles;
    private final String title;

    public PullRequest(Repository repository, String branchName, int number, String url, List<String> labels, List<String> changedFiles, String title) {
        this.repository = repository;
        this.branchName = branchName;
        this.number = number;
        this.url = url;
        this.labels = labels;
        this.changedFiles = changedFiles;
        this.title = title;
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
}
