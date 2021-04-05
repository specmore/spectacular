package spectacular.backend.cataloguemanifest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.validation.Validation;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import spectacular.backend.cataloguemanifest.model.Catalogue;
import spectacular.backend.cataloguemanifest.model.CatalogueManifest;
import spectacular.backend.github.domain.ContentItem;

@Component
public class CatalogueManifestParser {
  private static final Logger logger = LoggerFactory.getLogger(CatalogueManifestParser.class);
  private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
  private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  /**
   * Parses the YAML contents of a catalogue manifest file and returns the result.
   *
   * @param contentItem the github api content item holding the contents of a catalogue manifest file to be parsed
   * @return the CatalogueManifestContentItemParseResult
   */
  public CatalogueManifestContentItemParseResult parseManifestFileContentItem(ContentItem contentItem) {
    try {
      var fileContents = contentItem.getDecodedContent();
      var parseResult = parseManifestFileContents(fileContents);
      return CatalogueManifestContentItemParseResult.createFrom(parseResult, contentItem);
    } catch (UnsupportedEncodingException e) {
      logger.error("An error occurred while decoding the catalogue manifest yaml file at " + contentItem.getHtml_url().toString(), e);
      var error = "An error occurred while decoding the catalogue manifest yaml file contents.";
      return CatalogueManifestContentItemParseResult.createParseErrorResult(error, contentItem);
    }
  }

  private CatalogueManifestParseResult parseManifestFileContents(String manifestFileContents) {
    CatalogueManifest manifest = null;
    String error = null;
    try {
      manifest = mapper.readValue(manifestFileContents, CatalogueManifest.class);
    } catch (MismatchedInputException e) {
      logger.debug("A mapping error occurred while parsing a catalogue manifest yaml file. ", e);
      error = "A mapping error occurred while parsing the catalogue manifest yaml file. The following field is invalid: " +
          e.getPathReference();
    } catch (IOException e) {
      logger.error("An IO error occurred while parsing a catalogue manifest yaml file.", e);
      error = "An IO error occurred while parsing the catalogue manifest yaml file: " + e.getMessage();
    }

    if (error != null) {
      return CatalogueManifestParseResult.createParseErrorResult(error);
    }

    var violations = validator.validate(manifest);
    if (violations.size() > 0) {
      var violationMessage = violations.stream()
          .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
          .collect(Collectors.toList());
      error = "The following validation errors were found with the catalogue manifest file: " +
          String.join(", ", violationMessage);
      return CatalogueManifestParseResult.createParseErrorResult(error);
    }

    return CatalogueManifestParseResult.createSuccessfulCatalogueManifestParseResult(manifest);
  }

  /**
   * Finds a specific catalogue in the YAML contents of a catalogue manifest file and returns the parsed result.
   *
   * @param manifestFileContents the YAML contents of a catalogue manifest file to be searched and parsed
   * @param catalogueName the name of the specific catalogue to be found
   * @return the FindAndParseCatalogueResult with the catalogue manifest entry.
   *     If the catalogue entry could not be found, then a FindAndParseCatalogueResult with a null catalogue property is returned.
   *     If an error occurred while trying to find the catalogue entry, then a FindAndParseCatalogueResult with a null error message
   *     property is returned.
   */
  public FindAndParseCatalogueResult findAndParseCatalogueInManifestFileContents(String manifestFileContents, String catalogueName) {
    Catalogue catalogue = null;
    String error = null;
    try {
      var rootNode = mapper.readTree(manifestFileContents);
      var cataloguesNode = rootNode.get("catalogues");
      if (cataloguesNode == null) {
        logger.debug("Unable to find 'catalogues' root node catalogue manifest yaml file.");
        return FindAndParseCatalogueResult.createCatalogueEntryNotFoundResult();
      }
      var catalogueNode = cataloguesNode.get(catalogueName);
      if (catalogueNode == null) {
        logger.debug("Unable to find catalogue node '{}' in 'catalogues' node catalogue manifest yaml file.", catalogueName);
        return FindAndParseCatalogueResult.createCatalogueEntryNotFoundResult();
      }
      catalogue = mapper.treeToValue(catalogueNode, Catalogue.class);
    } catch (MismatchedInputException e) {
      logger.debug("A mapping error occurred while parsing a catalogue manifest yaml file. ", e);
      error = "A mapping error occurred while parsing the catalogue manifest yaml file. The following field is invalid: " +
          e.getPathReference();
    } catch (IOException e) {
      logger.error("An IO error occurred while parsing a catalogue manifest yaml file.", e);
      error = "An IO error occurred while parsing the catalogue manifest yaml file: " + e.getMessage();
    }

    if (error != null) {
      return FindAndParseCatalogueResult.createCatalogueEntryParseErrorResult(error);
    }

    var violations = validator.validate(catalogue);
    if (violations.size() > 0) {
      var violationMessage = violations.stream()
          .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
          .collect(Collectors.toList());
      error = "The following validation errors were found with catalogue entry '" + catalogueName + "': " +
          String.join(", ", violationMessage);
      return FindAndParseCatalogueResult.createCatalogueEntryParseErrorResult(error);
    }

    return FindAndParseCatalogueResult.createCatalogueEntryParsedResult(catalogue);
  }
}
