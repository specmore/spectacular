package spectacular.backend.catalogues;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import spectacular.backend.common.Repository;
import spectacular.backend.github.RestApiClient;
import spectacular.backend.github.app.AppInstallationContextProvider;
import spectacular.backend.github.domain.SearchCodeResultItem;
import spectacular.backend.pullrequests.PullRequest;
import spectacular.backend.pullrequests.PullRequestService;
import spectacular.backend.specs.SpecLog;
import spectacular.backend.specs.SpecLogService;

@Service
public class CatalogueService {
  private static final String CATALOGUE_MANIFEST_FILE_NAME = "spectacular-config";
  private static final String CATALOGUE_MANIFEST_FILE_EXTENSION = "yaml";
  private static final String CATALOGUE_MANIFEST_FILE_PATH = "/";
  private static final String CATALOGUE_MANIFEST_FULL_FILE_NAME =
      CATALOGUE_MANIFEST_FILE_NAME + "." + CATALOGUE_MANIFEST_FILE_EXTENSION;

  private static final Logger logger = LoggerFactory.getLogger(CatalogueService.class);

  private final RestApiClient restApiClient;
  private final AppInstallationContextProvider appInstallationContextProvider;
  private final SpecLogService specLogService;
  private final PullRequestService pullRequestService;

  /**
   * A service component that encapsulates all the logic required to build Catalogue objects from the information stored in git repositories
   * accessible for a given request's installation context.
   *
   * @param restApiClient an API client to retrieve information about the git repositories
   * @param appInstallationContextProvider a provider of the installation context in which any catalogue requests are being made
   * @param specLogService a service component providing the functionality to retrieve Spec Log items referenced within the catalogue
   * @param pullRequestService a service component providing the functionality to find all open Pull Requests changing the referenced spec
   *     files
   */
  public CatalogueService(RestApiClient restApiClient,
                          AppInstallationContextProvider appInstallationContextProvider,
                          SpecLogService specLogService, PullRequestService pullRequestService) {
    this.restApiClient = restApiClient;
    this.appInstallationContextProvider = appInstallationContextProvider;
    this.specLogService = specLogService;
    this.pullRequestService = pullRequestService;
  }

  /**
   * Get the list of all catalogues a given user can access in a specific organisation.
   *
   * @param orgName the name of the organisation
   * @param username the username of the user
   * @return a list of all the accessible catalogues
   */
  public List<Catalogue> getCataloguesForOrgAndUser(String orgName, String username) {
    var catalogues = findCatalogueRepositoriesForOrg(orgName).stream()
        .filter(repository -> isUserCollaboratorForRepository(repository, username))
        .map(this::getCatalogueForRepository).collect(Collectors.toList());
    return catalogues;
  }

  /**
   * Get a Catalogue stored in a specific repository for a given user.
   *
   * @param repo the repository the catalogue is stored in
   * @param username the username of the user
   * @return A Catalogue object
   */
  public Catalogue getCatalogueForRepoAndUser(Repository repo, String username) {
    if (!isUserCollaboratorForRepository(repo, username)) {
      return null;
    }

    var responseRepo = restApiClient.getRepository(repo);

    return getFullCatalogueDetailsForRepository(Repository.createRepositoryFrom(responseRepo));
  }

  private List<Repository> findCatalogueRepositoriesForOrg(String orgName) {
    var searchCodeResults = restApiClient.findFiles(CATALOGUE_MANIFEST_FILE_NAME, CATALOGUE_MANIFEST_FILE_EXTENSION,
            CATALOGUE_MANIFEST_FILE_PATH, orgName);
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
    var fileContentItem = restApiClient.getRepositoryContent(repository, CATALOGUE_MANIFEST_FULL_FILE_NAME, null);

    CatalogueManifest manifest = null;
    String error = null;
    try {
      manifest = CatalogueManifest.parse(fileContentItem.getDecodedContent());
    } catch (MismatchedInputException e) {
      logger.debug("An error occurred while parsing the catalogue manifest yaml file for repo: " + repository.getNameWithOwner(), e);
      error = "An error occurred while parsing the catalogue manifest yaml file. The following field is missing: " + e.getPathReference();
    } catch (IOException e) {
      logger.error("An error occurred while parsing the catalogue manifest yaml file for repo: " + repository.getNameWithOwner(), e);
      error = "An error occurred while parsing the catalogue manifest yaml file: " + e.getMessage();
    }

    return Catalogue.create(repository, manifest, error);
  }

  private Catalogue getFullCatalogueDetailsForRepository(Repository repository) {
    List<SpecLog> specLogs = null;
    var catalogue = getCatalogueForRepository(repository);

    if (catalogue.getCatalogueManifest() != null) {
      var specFileLocationsWithRepos = addCatalogueRepoToSpecFileLocationsWithoutRepo(
          catalogue.getCatalogueManifest().getSpecFileLocations(), repository);
      var repoPullRequests = getRepoPullRequestsForManifestSpecs(specFileLocationsWithRepos);
      specLogs = specFileLocationsWithRepos.stream()
          .map(specFileLocation -> getSpecLogForFileLocation(specFileLocation, repoPullRequests))
          .collect(Collectors.toList());
    }

    return catalogue.with(specLogs);
  }

  private List<SpecFileLocation> addCatalogueRepoToSpecFileLocationsWithoutRepo(List<SpecFileLocation> specFileLocations,
                                                                                Repository catalogueRepo) {
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
