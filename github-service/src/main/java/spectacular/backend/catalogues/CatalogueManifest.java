package spectacular.backend.catalogues;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.List;

public class CatalogueManifest {
    private final String name;
    private final String description;
    private final List<SpecFileLocation> specFileLocations;

    public CatalogueManifest(@JsonProperty(value = "name", required = true) String name,
                             @JsonProperty("description") String description,
                             @JsonProperty("spec-files") List<SpecFileLocation> specFileLocations) {
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

    public static CatalogueManifest parse(String manifestFile) throws JsonProcessingException {
        var mapper = new ObjectMapper(new YAMLFactory());

        return mapper.readValue(manifestFile, CatalogueManifest.class);
    }
}
