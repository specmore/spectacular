package spectacular.github.service.config.instance;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Catalogue {
    private final String repo;
    private final String name;

    public Catalogue(@JsonProperty("repo") String repo, @JsonProperty("name") String name) {
        this.repo = repo;
        this.name = name;
    }

    public String getRepo() {
        return repo;
    }

    public String getName() {
        return name;
    }
}
