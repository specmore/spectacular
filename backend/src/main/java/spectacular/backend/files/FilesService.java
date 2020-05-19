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
   *     or null if the spec file doesn't exist in the catalogue or the user does not have access to the catalogue
   * @throws UnsupportedEncodingException if an error is occurred when decoding the file contents returned by the git source system
   */
  public String getFileContent(Repository catalogueRepo, Repository specRepo, String path, String ref, String username)
      throws UnsupportedEncodingException {
    var isInCatalogue = catalogueService.isSpecFileInCatalogue(catalogueRepo, username, specRepo, path);

    if (!isInCatalogue) {
      return null;
    }

    return restApiClient.getRepositoryContent(specRepo, path, ref).getDecodedContent();
  }
}
