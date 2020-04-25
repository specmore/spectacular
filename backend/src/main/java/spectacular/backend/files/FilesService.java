package spectacular.backend.files;

import java.io.UnsupportedEncodingException;
import org.springframework.stereotype.Service;
import spectacular.backend.catalogues.CatalogueService;
import spectacular.backend.catalogues.SpecFileLocation;
import spectacular.backend.common.Repository;
import spectacular.backend.github.RestApiClient;

@Service
public class FilesService {
  private final CatalogueService catalogueService;
  private final RestApiClient restApiClient;

  public FilesService(CatalogueService catalogueService, RestApiClient restApiClient) {
    this.catalogueService = catalogueService;
    this.restApiClient = restApiClient;
  }

  /**
   * Get the contents of a file stored in a repository that is listed in a catalogue for a given user's access.
   *
   * @param catalogueRepo the repository of the catalogue the file is listed in
   * @param specRepo the repository the file is in
   * @param path the path to the file in the repository
   * @param ref the commit ref version of the file contents
   * @param username the username of the user the access should be verified against
   * @return the file contents as a String
   * @throws UnsupportedEncodingException if an error is occurred when decoding the file contents returned by the git source system
   */
  public String getFileContent(Repository catalogueRepo, Repository specRepo, String path, String ref, String username)
      throws UnsupportedEncodingException {
    var catalogue = catalogueService.getCatalogueForRepoAndUser(catalogueRepo, username);

    if (catalogue == null || catalogue.getCatalogueManifest() == null || catalogue.getCatalogueManifest().getSpecFileLocations() == null) {
      return null;
    }

    if (!catalogue.getCatalogueManifest().getSpecFileLocations().stream()
        .anyMatch(specFileLocation -> specFileMatches(specFileLocation, catalogueRepo, specRepo, path))) {
      return null;
    }

    return restApiClient.getRepositoryContent(specRepo, path, ref).getDecodedContent();
  }

  private static boolean specFileMatches(SpecFileLocation specFileLocation, Repository catalogueRepo, Repository specRepo,
                                         String specFilePath) {
    if (specFileLocation.getRepo() == null && !catalogueRepo.equals(specRepo)) {
      return false;
    }
    if (specFileLocation.getRepo() != null && !specFileLocation.getRepo().equals(specRepo)) {
      return false;
    }
    return specFileLocation.getFilePath().equalsIgnoreCase(specFilePath);
  }
}
