package spectacular.backend.github.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Repository {
    private final int id;
    private final String full_name;
    private final String html_url;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Repository(@JsonProperty("id") int id,
                      @JsonProperty("full_name") String full_name,
                      @JsonProperty("html_url") String html_url) {
        this.id = id;
        this.full_name = full_name;
        this.html_url = html_url;
    }

    public int getId() {
        return id;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getHtml_url() {
        return html_url;
    }
}