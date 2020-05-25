package spectacular.backend.cataloguemanifest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spectacular.backend.cataloguemanifest.model.Catalogue;
import spectacular.backend.cataloguemanifest.model.CatalogueManifest;

public class CatalogueManifestParser {
  private static final Logger logger = LoggerFactory.getLogger(CatalogueManifestParser.class);

  public static CatalogueManifestParseResult parseManifestFileContents(String manifestFileContents) {
    var mapper = new ObjectMapper(new YAMLFactory());

    CatalogueManifest manifest = null;
    String error = null;
    try {
      manifest = mapper.readValue(manifestFileContents, CatalogueManifest.class);
    } catch (MismatchedInputException e) {
      logger.debug("A mapping error occurred while parsing a catalogue manifest yaml file. ", e);
      error = "A mapping error occurred while parsing the catalogue manifest yaml file. The following field is missing: " + e.getPathReference();
    } catch (IOException e) {
      logger.error("An IO error occurred while parsing a catalogue manifest yaml file.", e);
      error = "An IO error occurred while parsing the catalogue manifest yaml file: " + e.getMessage();
    }

    return new CatalogueManifestParseResult(manifest, error);
  }

  public static CatalogueParseResult findAndParseCatalogueInManifestFileContents(String manifestFileContents, String catalogueName) {
    var mapper = new ObjectMapper(new YAMLFactory());

    Catalogue catalogue = null;
    String error = null;
    try {
      var rootNode = mapper.readTree(manifestFileContents);
      var cataloguesNode = rootNode.get("catalogues");
      if (cataloguesNode == null) {
        error = "Unable to find 'catalogues' root node catalogue manifest yaml file.";
        logger.debug(error);
        return new CatalogueParseResult(null, error);
      }
      var catalogueNode = cataloguesNode.get(catalogueName);
      if (catalogueNode == null) {
        error = String.format("Unable to find catalogue node '%s' in 'catalogues' node catalogue manifest yaml file.", catalogueName);;
        logger.debug(error);
        return new CatalogueParseResult(null, error);
      }
      catalogue = mapper.treeToValue(catalogueNode, Catalogue.class);
    } catch (MismatchedInputException e) {
      logger.debug("A mapping error occurred while parsing a catalogue manifest yaml file. ", e);
      error = "A mapping error occurred while parsing the catalogue manifest yaml file. The following field is missing: " + e.getPathReference();
    } catch (IOException e) {
      logger.error("An IO error occurred while parsing a catalogue manifest yaml file.", e);
      error = "An IO error occurred while parsing the catalogue manifest yaml file: " + e.getMessage();
    }

    return new CatalogueParseResult(catalogue, error);
  }
}
