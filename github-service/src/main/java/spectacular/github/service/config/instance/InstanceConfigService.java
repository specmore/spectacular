package spectacular.github.service.config.instance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import spectacular.github.service.github.RestApiClient;
import spectacular.github.service.github.app.AppAuthenticationService;
import spectacular.github.service.github.app.AppInstallationContextProvider;
import spectacular.github.service.common.Repository;
import spectacular.github.service.github.domain.SearchCodeResultItem;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstanceConfigService {
    private static final String INSTANCE_CONFIG_FILE_PATH = "spectacular-app-config.yaml";

    private static final Logger logger = LoggerFactory.getLogger(AppAuthenticationService.class);

    private final RestApiClient restApiClient;
    private final AppInstallationContextProvider appInstallationContextProvider;

    public InstanceConfigService(RestApiClient restApiClient, AppInstallationContextProvider appInstallationContextProvider) {
        this.restApiClient = restApiClient;
        this.appInstallationContextProvider = appInstallationContextProvider;
    }

    public List<InstanceConfig> getInstanceConfigsForInstallation() {
        var instanceConfigs = findInstanceConfigRepositoriesForInstallation().stream().map(this::getInstanceConfigForRepository).collect(Collectors.toList());
        return instanceConfigs;
    }

    public InstanceConfig getInstanceConfigForRepository(Repository repository) {
        var fileContents = restApiClient.getRepositoryContent(repository, INSTANCE_CONFIG_FILE_PATH, null);

        var mapper = new ObjectMapper(new YAMLFactory());
        InstanceConfigManifest manifest = null;
        String error = null;
        try {
            manifest = mapper.readValue(fileContents, InstanceConfigManifest.class);
        } catch (IOException e) {
            logger.error("An error occurred while parsing a instance config yaml file", e);
            error = "An error occurred while parsing a instance config yaml file: " + e.getMessage();
        }

        return new InstanceConfig(appInstallationContextProvider.getInstallationId(), repository, manifest, error);
    }

    public List<Repository> findInstanceConfigRepositoriesForInstallation() {
        var searchCodeResults = restApiClient.findFiles(INSTANCE_CONFIG_FILE_PATH);
        return searchCodeResults.getItems().stream().map(InstanceConfigService::createRepositoryFrom).collect(Collectors.toList());
    }

    private static Repository createRepositoryFrom(SearchCodeResultItem searchCodeResultItem) {
        return new Repository(searchCodeResultItem.getRepository().getFull_name());
    }
}
