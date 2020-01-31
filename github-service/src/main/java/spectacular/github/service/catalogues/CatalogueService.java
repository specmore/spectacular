package spectacular.github.service.catalogues;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import spectacular.github.service.common.Repository;
import spectacular.github.service.github.RestApiClient;
import spectacular.github.service.github.app.AppInstallationContextProvider;
import spectacular.github.service.github.domain.SearchCodeResultItem;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogueService {
    private static final String CATALOGUE_MANIFEST_FILE_NAME = "spectacular-config";
    private static final String CATALOGUE_MANIFEST_FILE_EXTENSION = "yaml";
    private static final String CATALOGUE_MANIFEST_FILE_PATH = "/";
    private static final String CATALOGUE_MANIFEST_FULL_FILE_NAME = CATALOGUE_MANIFEST_FILE_NAME + "." + CATALOGUE_MANIFEST_FILE_EXTENSION;

    private static final Logger logger = LoggerFactory.getLogger(CatalogueService.class);

    private final RestApiClient restApiClient;
    private final AppInstallationContextProvider appInstallationContextProvider;

    public CatalogueService(RestApiClient restApiClient, AppInstallationContextProvider appInstallationContextProvider) {
        this.restApiClient = restApiClient;
        this.appInstallationContextProvider = appInstallationContextProvider;
    }

    public List<Catalogue> getCataloguesForOrgAndUser(String orgName, String username) {
        var catalogues = findCatalogueRepositoriesForOrg(orgName).stream()
                .filter(repository -> isUserCollaboratorForRepository(repository, username))
                .map(this::getInstanceConfigForRepository).collect(Collectors.toList());
        return catalogues;
    }

    private List<Repository> findCatalogueRepositoriesForOrg(String orgName) {
        var searchCodeResults = restApiClient.findFiles(CATALOGUE_MANIFEST_FILE_NAME, CATALOGUE_MANIFEST_FILE_EXTENSION, CATALOGUE_MANIFEST_FILE_PATH, orgName);
        logger.debug("find catalogue manifest files results for org '" + orgName + "': " + searchCodeResults.toString());
        return searchCodeResults.getItems().stream()
                .filter(resultItem -> isExactFileNameMatch(resultItem, CATALOGUE_MANIFEST_FULL_FILE_NAME))
                .map(Repository::createRepositoryFrom)
                .collect(Collectors.toList());
    }

    private boolean isExactFileNameMatch(SearchCodeResultItem searchCodeResultItem, String filename) {
        return searchCodeResultItem.getName().equals(filename);
    }

    private boolean isUserCollaboratorForRepository(Repository repo, String username) {
        return restApiClient.isUserRepositoryCollaborator(repo, username);
    }

    private Catalogue getInstanceConfigForRepository(Repository repository) {
        var fileContents = restApiClient.getRepositoryContent(repository, CATALOGUE_MANIFEST_FULL_FILE_NAME, null);

        var mapper = new ObjectMapper(new YAMLFactory());
        CatalogueManifest manifest = null;
        String error = null;
        try {
            manifest = mapper.readValue(fileContents, CatalogueManifest.class);
        } catch (MismatchedInputException e) {
            logger.error("An error occurred while parsing the catalogue manifest yaml file for repo: " + repository.getNameWithOwner(), e);
            error = "An error occurred while parsing the catalogue manifest yaml file. The following field is missing: " + e.getPathReference();
        } catch (IOException e) {
            logger.error("An error occurred while parsing the catalogue manifest yaml file for repo: " + repository.getNameWithOwner(), e);
            error = "An error occurred while parsing the catalogue manifest yaml file: " + e.getMessage();
        }

        return new Catalogue(repository, manifest, error);
    }
}
