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

  /**
   * A model representing the contents of a Catalogue Manifest file found inside a repository used to describe a Catalogue.#
   *
   * @param name The name of the catalogue
   * @param description A description of the catalogue
   * @param specFileLocations The location of the spec files belonging to this catalogue
   */
  public CatalogueManifest(@JsonProperty(value = "name", required = true) String name,
                           @JsonProperty("description") String description,
                           @JsonProperty("spec-files") List<SpecFileLocation> specFileLocations) {
    this.name = name;
    this.description = description;
    this.specFileLocations = specFileLocations;
  }

  /**
   * A factory method to parse and deserialize a CatalogueManifest object from the string contents of a manifest file.
   *
   * @param manifestFileContents the string contents of the manifest file
   * @return a Catalogue Manifest model object
   * @throws JsonProcessingException representing an error found while de-serializing the manifest file content
   */
  public static CatalogueManifest parse(String manifestFileContents) throws JsonProcessingException {
    var mapper = new ObjectMapper(new YAMLFactory());

    return mapper.readValue(manifestFileContents, CatalogueManifest.class);
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
