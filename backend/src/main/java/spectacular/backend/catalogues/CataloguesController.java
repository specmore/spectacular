package spectacular.backend.catalogues;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import spectacular.backend.api.CataloguesApi;
import spectacular.backend.api.model.FindCataloguesResult;
import spectacular.backend.api.model.GetCatalogueResult;

public class CataloguesController implements CataloguesApi {
  private final CatalogueService catalogueService;
  private final JwtAuthenticationToken authToken;

  public CataloguesController(CatalogueService catalogueService,
                              JwtAuthenticationToken authToken) {
    this.catalogueService = catalogueService;
    this.authToken = authToken;
  }

  @Override
  public ResponseEntity<FindCataloguesResult> findCataloguesForUser(@NotNull @Valid String org) {
    var catalogues = catalogueService.findCataloguesForOrgAndUser(org, authToken.getName());
    var findCataloguesResult = new FindCataloguesResult()
        .catalogues(catalogues);
    return ResponseEntity.ok(findCataloguesResult);
  }

  @Override
  public ResponseEntity<GetCatalogueResult> getCatalogue(byte[] encoded) {
    var catalogueId = CatalogueId.createFrom(new String(encoded));
    var catalogue = catalogueService.getCatalogueForUser(catalogueId, authToken.getName());
    if (catalogue == null) {
      ResponseEntity.notFound();
    }
    var getCatalogueResult = new GetCatalogueResult()
        .catalogue(catalogue);
    return ResponseEntity.ok(getCatalogueResult);
  }
}
