package spectacular.github.service.config.instance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Service;
import spectacular.github.service.github.RestApiClient;
import spectacular.github.service.github.app.AppInstallationContextProvider;
import spectacular.github.service.github.domain.Repository;

import java.io.IOException;

@Service
public class InstanceConfigService {
    private static final String INSTANCE_CONFIG_FILE_PATH = "spectacular-app-config.yaml";

    private final RestApiClient restApiClient;
    private final AppInstallationContextProvider appInstallationContextProvider;

    public InstanceConfigService(RestApiClient restApiClient, AppInstallationContextProvider appInstallationContextProvider) {
        this.restApiClient = restApiClient;
        this.appInstallationContextProvider = appInstallationContextProvider;
    }

    public InstanceConfig getInstanceConfigForRepository(Repository repository) throws IOException {
        var fileContents = restApiClient.getRepositoryContent(repository, INSTANCE_CONFIG_FILE_PATH, null);

        var mapper = new ObjectMapper(new YAMLFactory());
        var manifest = mapper.readValue(fileContents, InstanceConfigManifest.class);
        return new InstanceConfig(appInstallationContextProvider.getInstallationId(), repository, manifest);
    }
}
