package spectacular.github.service.specs;

import org.springframework.stereotype.Service;
import spectacular.github.service.common.Repository;
import spectacular.github.service.github.RestApiClient;
import spectacular.github.service.specs.openapi.OpenApiParser;

@Service
public class SpecService {
    private final RestApiClient restApiClient;

    public SpecService(RestApiClient restApiClient) {
        this.restApiClient = restApiClient;
    }

    public SpecItem getSpecItem(Repository repo, String filePath) {
        String fileContent = restApiClient.getRepositoryContent(repo, filePath, null);
        var parseResult = OpenApiParser.parseYAML(fileContent);
        return new SpecItem(repo, filePath, parseResult);
    }
}
