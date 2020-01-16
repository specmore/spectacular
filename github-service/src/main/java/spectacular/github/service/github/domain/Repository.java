package spectacular.github.service.github.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Repository {
    private final int id;
    private final String full_name;

    public Repository(@JsonProperty("id") int id, @JsonProperty("full_name") String full_name) {
        this.id = id;
        this.full_name = full_name;
    }

    public int getId() {
        return id;
    }

    public String getFull_name() {
        return full_name;
    }
}
