package spectacular.github.service.config.instance;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class InstanceConfigManifest {
    private final List<Catalogue> catalogues;

    public InstanceConfigManifest(@JsonProperty("catalogues") List<Catalogue> catalogues) {
        this.catalogues = catalogues;
    }

    public List<Catalogue> getCatalogues() {
        return catalogues;
    }
}
