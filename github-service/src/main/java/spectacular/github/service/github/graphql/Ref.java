package spectacular.github.service.github.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ref {
    private final String name;
    private final Repository repository;

    public Ref(@JsonProperty("name") String name, @JsonProperty("repository") Repository repository) {
        this.name = name;
        this.repository = repository;
    }

    public String getName() {
        return name;
    }

    public Repository getRepository() {
        return repository;
    }
}
