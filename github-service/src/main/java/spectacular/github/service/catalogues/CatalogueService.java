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
import spectacular.github.service.pullrequests.PullRequest;
import spectacular.github.service.pullrequests.PullRequestService;
import spectacular.github.service.specs.SpecLog;
import spectacular.github.service.specs.SpecLogService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
    private final SpecLogService specLogService;
    private final PullRequestService pullRequestService;

    public CatalogueService(RestApiClient restApiClient, AppInstallationContextProvider appInstallationContextProvider, SpecLogService specLogService, PullRequestService pullRequestService) {
        this.restApiClient = restApiClient;
        this.appInstallationContextProvider = appInstallationContextProvider;
        this.specLogService = specLogService;
        this.pullRequestService = pullRequestService;
    }

    public List<Catalogue> getCataloguesForOrgAndUser(String orgName, String username) {
        var catalogues = findCatalogueRepositoriesForOrg(orgName).stream()
                .filter(repository -> isUserCollaboratorForRepository(repository, username))
                .map(this::getCatalogueForRepository).collect(Collectors.toList());
        return catalogues;
    }

    public Catalogue getCatalogueForRepoAndUser(Repository repo, String username) {
        if (!isUserCollaboratorForRepository(repo, username)) return null;

        var responseRepo = restApiClient.getRepository(repo);

        return getFullCatalogueDetailsForRepository(Repository.createRepositoryFrom(responseRepo));
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

    private Catalogue getCatalogueForRepository(Repository repository) {
        var fileContents = restApiClient.getRawRepositoryContent(repository, CATALOGUE_MANIFEST_FULL_FILE_NAME, null);

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

        return new Catalogue(repository, manifest, null, error);
    }

    private Catalogue getFullCatalogueDetailsForRepository(Repository repository) {
        var fileContents = restApiClient.getRawRepositoryContent(repository, CATALOGUE_MANIFEST_FULL_FILE_NAME, null);

        var mapper = new ObjectMapper(new YAMLFactory());
        CatalogueManifest manifest = null;
        List<SpecLog> specLogs = null;
        String error = null;
        try {
            manifest = mapper.readValue(fileContents, CatalogueManifest.class);
        } catch (MismatchedInputException e) {
            logger.debug("An error occurred while parsing the catalogue manifest yaml file for repo: " + repository.getNameWithOwner(), e);
            error = "An error occurred while parsing the catalogue manifest yaml file. The following field is missing: " + e.getPathReference();
        } catch (IOException e) {
            logger.error("An unexpected error occurred while parsing the catalogue manifest yaml file for repo: " + repository.getNameWithOwner(), e);
            error = "An error occurred while parsing the catalogue manifest yaml file: " + e.getMessage();
        }

        if (manifest != null) {
            var specFileLocationsWithRepos = addCatalogueRepoToSpecFileLocationsWithoutRepo(manifest.getSpecFileLocations(), repository);
            var repoPullRequests = getRepoPullRequestsForManifestSpecs(specFileLocationsWithRepos);
            specLogs = specFileLocationsWithRepos.stream().map(specFileLocation -> getSpecLogForFileLocation(specFileLocation, repoPullRequests)).collect(Collectors.toList());
        }

        return new Catalogue(repository, manifest, specLogs, error);
    }

    private List<SpecFileLocation> addCatalogueRepoToSpecFileLocationsWithoutRepo(List<SpecFileLocation> specFileLocations, Repository catalogueRepo) {
        return specFileLocations.stream().map(specFileLocation -> {
            var specRepo = specFileLocation.getRepo() != null ? specFileLocation.getRepo() : catalogueRepo;
            return new SpecFileLocation(specRepo, specFileLocation.getFilePath());
        }).collect(Collectors.toList());
    }

    private Map<Repository, List<PullRequest>> getRepoPullRequestsForManifestSpecs(List<SpecFileLocation> specFileLocations) {
        var uniqueRepos = specFileLocations.stream().map(specFileLocation -> specFileLocation.getRepo()).distinct();
        return uniqueRepos.collect(Collectors.toMap(Function.identity(), repository -> pullRequestService.getPullRequestsForRepo(repository)));
    }

    private SpecLog getSpecLogForFileLocation(SpecFileLocation specFileLocation, Map<Repository, List<PullRequest>> repoPullRequests) {
        var specFilePath = specFileLocation.getFilePath();
        var specRepo = specFileLocation.getRepo();
        var pullRequests = repoPullRequests.get(specRepo);

        return specLogService.getSpecLogForSpecRepoAndFile(specRepo, specFilePath, pullRequests);
    }
}
