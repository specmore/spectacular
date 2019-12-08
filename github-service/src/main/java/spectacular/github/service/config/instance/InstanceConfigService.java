package spectacular.github.service.config.instance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import spectacular.github.service.github.RestApiClient;
import spectacular.github.service.github.domain.Repository;

import java.io.IOException;

public class InstanceConfigService {
    private static final String INSTANCE_CONFIG_FILE_PATH = "spectacular-app-config.yaml";

    private final RestApiClient restApiClient;

    public InstanceConfigService(RestApiClient restApiClient) {
        this.restApiClient = restApiClient;
    }

    public InstanceConfig getInstanceConfigForRepositoryAndInstallation(Repository repository, String installationId) throws IOException {
        var fileContents = restApiClient.getRepositoryContent(repository, INSTANCE_CONFIG_FILE_PATH, null);

        var mapper = new ObjectMapper(new YAMLFactory());
        var manifest = mapper.readValue(fileContents, InstanceConfigManifest.class);
        return new InstanceConfig(installationId, repository, manifest);
    }
}
