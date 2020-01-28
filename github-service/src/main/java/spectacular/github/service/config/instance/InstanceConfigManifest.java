package spectacular.github.service.config.instance;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class InstanceConfigManifest {
    private final String name;
    private final List<Catalogue> catalogues;

    public InstanceConfigManifest(@JsonProperty("name") String name, @JsonProperty("catalogues") List<Catalogue> catalogues) {
        this.name = name;
        this.catalogues = catalogues;
    }

    public List<Catalogue> getCatalogues() {
        return catalogues;
    }

    public String getName() {
        return name;
    }
}
