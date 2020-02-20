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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstanceConfigService {
    private static final String INSTANCE_CONFIG_FILE_NAME = "spectacular-app-config";
    private static final String INSTANCE_CONFIG_FILE_EXTENSION = "yaml";
    private static final String INSTANCE_CONFIG_FILE_PATH = "/";
    private static final String INSTANCE_CONFIG_FULL_FILE_NAME = INSTANCE_CONFIG_FILE_NAME + "." + INSTANCE_CONFIG_FILE_EXTENSION;

    private static final Logger logger = LoggerFactory.getLogger(AppAuthenticationService.class);

    private final RestApiClient restApiClient;
    private final AppInstallationContextProvider appInstallationContextProvider;

    public InstanceConfigService(RestApiClient restApiClient, AppInstallationContextProvider appInstallationContextProvider) {
        this.restApiClient = restApiClient;
        this.appInstallationContextProvider = appInstallationContextProvider;
    }

    public List<InstanceConfig> getInstanceConfigsForUser(String username) {
        var instanceConfigs = findInstanceConfigRepositories().stream()
                .filter(repository -> isUserCollaboratorForRepository(repository, username))
                .map(this::getInstanceConfigForRepository).collect(Collectors.toList());
        return instanceConfigs;
    }

    public InstanceConfig getInstanceConfigForRepository(Repository repository) {
        var fileContents = restApiClient.getRawRepositoryContent(repository, INSTANCE_CONFIG_FULL_FILE_NAME, null);

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

    private List<Repository> findInstanceConfigRepositories() {
        var searchCodeResults = restApiClient.findFiles(INSTANCE_CONFIG_FILE_NAME, INSTANCE_CONFIG_FILE_EXTENSION, INSTANCE_CONFIG_FILE_PATH, null);
        return searchCodeResults.getItems().stream().map(Repository::createRepositoryFrom).collect(Collectors.toList());
    }

    private boolean isUserCollaboratorForRepository(Repository repo, String username) {
        return restApiClient.isUserRepositoryCollaborator(repo, username);
    }
}
