package spectacular.github.service.catalogues;

import com.fasterxml.jackson.annotation.JsonProperty;
import spectacular.github.service.common.Repository;

public class SpecFileLocation {
    private final Repository repo;
    private final String filePath;

    public SpecFileLocation(@JsonProperty("repo") String repo,
                            @JsonProperty(value = "file-path", required = true) String filePath) {
        this.repo = repo != null && !repo.isBlank() ? Repository.createForNameWithOwner(repo, null) : null;
        this.filePath = filePath;
    }

    public Repository getRepo() {
        return repo;
    }

    public String getFilePath() {
        return filePath;
    }
}
