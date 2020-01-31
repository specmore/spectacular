package spectacular.github.service.catalogues;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CatalogueManifest {
    private final String name;
    private final String description;
    private final List<SpecFileLocation> specFileLocations;

    public CatalogueManifest(@JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("spec-files") List<SpecFileLocation> specFileLocations) {
        this.name = name;
        this.description = description;
        this.specFileLocations = specFileLocations;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<SpecFileLocation> getSpecFileLocations() {
        return specFileLocations;
    }
}
