package spectacular.github.service.catalogues;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpecFileLocation {
    private final String repo;
    private final String filePath;

    public SpecFileLocation(@JsonProperty("repo") String repo, @JsonProperty("file-path") String filePath) {
        this.repo = repo;
        this.filePath = filePath;
    }

    public String getRepo() {
        return repo;
    }

    public String getFilePath() {
        return filePath;
    }
}
