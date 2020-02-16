package spectacular.github.service.files;

import org.springframework.stereotype.Service;
import spectacular.github.service.catalogues.CatalogueService;
import spectacular.github.service.common.Repository;
import spectacular.github.service.github.RestApiClient;

@Service
public class FilesService {
    private final CatalogueService catalogueService;
    private final RestApiClient restApiClient;

    public FilesService(CatalogueService catalogueService, RestApiClient restApiClient) {
        this.catalogueService = catalogueService;
        this.restApiClient = restApiClient;
    }

    public String getFileContent(Repository catalogueRepo, Repository specRepo, String path, String username) {
        var catalogue = catalogueService.getCatalogueForRepoAndUser(catalogueRepo, username);

        if (catalogue == null || catalogue.getCatalogueManifest() == null || catalogue.getCatalogueManifest().getSpecFileLocations() == null) return null;

        if (!catalogue.getCatalogueManifest().getSpecFileLocations().stream()
                .anyMatch(specFileLocation -> specFileLocation.getRepo().equals(specRepo) &&
                specFileLocation.getFilePath().equalsIgnoreCase(path))) return null;

        return restApiClient.getRepositoryContent(specRepo, path, null);
    }
}
