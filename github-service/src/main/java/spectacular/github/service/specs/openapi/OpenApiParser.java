package spectacular.github.service.specs.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class OpenApiParser {
    private static final Logger logger = LoggerFactory.getLogger(OpenApiParser.class);

    public static OpenApiSpecParseResult parseYAML(String yaml) {
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

                    return new OpenApiSpecParseResult(new OpenApiSpec(title, version, operationList), errorList);
                }
            }
        } catch (IOException e) {
            logger.debug("an io error occurred while parsing yaml contents", e);
        }
        return new OpenApiSpecParseResult(null, errorList);
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
        var topicName = operationField.getValue().path("x-topic-name").asText();
        return new OpenApiOperation(pathKey, operationField.getKey(), topicName);
    }
}
