package spectacular.backend.interfaces;

import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import spectacular.backend.catalogues.CatalogueService;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.common.RepositoryId;
import spectacular.backend.github.RestApiClient;

@Service
public class InterfaceService {
  private static final Logger logger = LoggerFactory.getLogger(InterfaceService.class);

  private final CatalogueService catalogueService;
  private final RestApiClient restApiClient;

  public InterfaceService(CatalogueService catalogueService, RestApiClient restApiClient) {
    this.catalogueService = catalogueService;
    this.restApiClient = restApiClient;
  }

  public InterfaceFileContents GetInterfaceFileContents(CatalogueId catalogueId, String interfaceName, String ref, String username)
      throws UnsupportedEncodingException {
    var catalogueInterfaceEntry = this.catalogueService.getInterfaceEntry(catalogueId, interfaceName,username);

    if (catalogueInterfaceEntry == null || catalogueInterfaceEntry.getSpecFile() == null) {
      return null;
    }

    RepositoryId fileRepo;
    if (catalogueInterfaceEntry.getSpecFile().getRepo() != null) {
      fileRepo = RepositoryId.createForNameWithOwner(catalogueInterfaceEntry.getSpecFile().getRepo());
    } else {
      fileRepo = catalogueId.getRepositoryId();
    }
    var filePath = catalogueInterfaceEntry.getSpecFile().getFilePath();

    try {
      var fileContentItem = restApiClient.getRepositoryContent(fileRepo, filePath, ref);
      var fileContents = fileContentItem.getDecodedContent();

      return new InterfaceFileContents(fileContents, filePath);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        logger.debug("A request for a interface spec file that does not exist was received. Spec File Location: {}", catalogueInterfaceEntry.getSpecFile());
        return null;
      } else {
        throw e;
      }
    }
  }
}
