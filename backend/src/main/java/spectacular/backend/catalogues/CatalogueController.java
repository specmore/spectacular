package spectacular.backend.catalogues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import spectacular.backend.common.Repository;

@RestController
public class CatalogueController {
  private static final Logger logger = LoggerFactory.getLogger(CatalogueController.class);
  private final CatalogueService catalogueService;

  public CatalogueController(CatalogueService catalogueService) {
    this.catalogueService = catalogueService;
  }

  @GetMapping("api/{org}/catalogues")
  public CataloguesResponse getCataloguesForOrg(@PathVariable("org") String orgName,
                                                JwtAuthenticationToken authToken) {
    var catalogues = this.catalogueService.getCataloguesForOrgAndUser(orgName, authToken.getName());
    return new CataloguesResponse(catalogues);
  }

  /**
   * Get the Catalogue found in the repository identified by it's owner and name.
   *
   * @param owner the owner name that identifies repository the catalogue can be found in
   * @param repo the repository name that identifies repository the catalogue can be found in
   * @param authToken the Authentication Token for the security principal of the request (to be provided by the authentication framework)
   * @return
   */
  @GetMapping("api/catalogues/{owner}/{repo}")
  public Catalogue getCatalogue(@PathVariable("owner") String owner,
                                @PathVariable("repo") String repo,
                                JwtAuthenticationToken authToken) {
    var repository = new Repository(owner, repo);
    var catalogue = this.catalogueService.getCatalogueForRepoAndUser(repository, authToken.getName());

    if (catalogue == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return catalogue;
  }
}
