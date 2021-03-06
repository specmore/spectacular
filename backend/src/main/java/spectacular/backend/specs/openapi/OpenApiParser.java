package spectacular.backend.specs.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spectacular.backend.api.model.OpenApiOperation;
import spectacular.backend.api.model.OpenApiSpec;
import spectacular.backend.api.model.OpenApiSpecParseResult;

public class OpenApiParser {
  private static final Logger logger = LoggerFactory.getLogger(OpenApiParser.class);

  /**
   * Reads the YAML contents of an OpenAPI file and find specific nodes required to create an OpenApiSpecParseResult object.
   *
   * @param yaml the OpenAPI YAML file's contents
   * @return a OpenApiSpecParseResult object with the values found in the yaml and any errors occurred
   */
  public static OpenApiSpecParseResult parseYaml(String yaml) {
    //todo: refactor using a builder pattern
    List<String> errorList = new ArrayList<>();
    try {
      JsonNode rootNode = new YAMLMapper().readTree(yaml);
      if (rootNode == null) {
        errorList.add("Missing root node");
      } else {
        JsonNode infoNode = rootNode.path("info");
        if (infoNode.isMissingNode()) {
          errorList.add("Missing info node");
        } else {
          String title = infoNode.path("title").asText();
          String version = infoNode.path("version").asText();
          var operationList = getOperationsFromRoot(rootNode, errorList);
          var spec = new OpenApiSpec().title(title).version(version).operations(operationList);
          return new OpenApiSpecParseResult().openApiSpec(spec).errors(errorList);
        }
      }
    } catch (IOException e) {
      logger.debug("an io error occurred while parsing yaml contents", e);
    }
    return new OpenApiSpecParseResult().errors(errorList);
  }

  private static List<OpenApiOperation> getOperationsFromRoot(JsonNode rootNode, List<String> errorList) {
    List<OpenApiOperation> operationList = new ArrayList<>();
    JsonNode pathsNode = rootNode.get("paths");

    if (pathsNode.isMissingNode()) {
      errorList.add("Missing path node");
      return operationList;
    }
    if (!pathsNode.isObject()) {
      errorList.add("Invalid path node");
      return operationList;
    }

    var pathFieldsSpliterator = Spliterators.spliteratorUnknownSize(pathsNode.fields(), Spliterator.ORDERED);
    return StreamSupport.stream(pathFieldsSpliterator, false)
        .map(OpenApiParser::getOperationsFromPath)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private static List<OpenApiOperation> getOperationsFromPath(Map.Entry<String, JsonNode> pathField) {
    var operationFieldsSpliterator = Spliterators.spliteratorUnknownSize(pathField.getValue().fields(), Spliterator.ORDERED);
    return StreamSupport.stream(operationFieldsSpliterator, false)
        .map(operation -> createOperationFromOperation(operation, pathField.getKey()))
        .collect(Collectors.toList());
  }

  private static OpenApiOperation createOperationFromOperation(Map.Entry<String, JsonNode> operationField, String pathKey) {
    //var topicName = operationField.getValue().path("x-topic-name").asText();
    return new OpenApiOperation().name(operationField.getKey()).path(pathKey);
  }
}
