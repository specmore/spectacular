package spectacular.github.service.specs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import spectacular.github.service.common.Repository;
import spectacular.github.service.github.RestApiClient;
import spectacular.github.service.specs.openapi.OpenApiParser;
import spectacular.github.service.specs.openapi.OpenApiSpecParseResult;

import java.util.List;

@Service
public class SpecService {
    private static final Logger logger = LoggerFactory.getLogger(SpecService.class);
    private final RestApiClient restApiClient;

    public SpecService(RestApiClient restApiClient) {
        this.restApiClient = restApiClient;
    }

    public SpecItem getSpecItem(Repository repo, String filePath) {
        OpenApiSpecParseResult parseResult = null;
        try {
            var fileContent = restApiClient.getRawRepositoryContent(repo, filePath, null);
            parseResult = OpenApiParser.parseYAML(fileContent);
        } catch (HttpClientErrorException.NotFound nf) {
            logger.debug("Failed to retrieve file contents due an file not found on the github api.", nf);
            parseResult = new OpenApiSpecParseResult(null, List.of("The spec file could not be found."));
        }

        return new SpecItem(repo, filePath, parseResult);
    }
}
