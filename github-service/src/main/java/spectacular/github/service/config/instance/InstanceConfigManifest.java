package spectacular.github.service.config.instance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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

    public static InstanceConfigManifest parse(String instanceConfigFile) throws JsonProcessingException {
        var mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(instanceConfigFile, InstanceConfigManifest.class);
    }
}
