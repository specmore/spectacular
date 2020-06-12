package spectacular.backend.catalogues;

import java.util.Base64;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;
import spectacular.backend.api.CataloguesApi;
import spectacular.backend.api.model.FindCataloguesResult;
import spectacular.backend.api.model.GetCatalogueResult;
import spectacular.backend.common.CatalogueId;

@RestController
public class CataloguesController implements CataloguesApi {
  private final CatalogueService catalogueService;

  public CataloguesController(CatalogueService catalogueService) {
    this.catalogueService = catalogueService;
  }

  @Override
  public ResponseEntity<FindCataloguesResult> findCataloguesForUser(@NotNull @Valid String org) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var catalogues = catalogueService.findCataloguesForOrgAndUser(org, authentication.getName());
    var findCataloguesResult = new FindCataloguesResult()
        .catalogues(catalogues);
    return ResponseEntity.ok(findCataloguesResult);
  }

  @Override
  public ResponseEntity<GetCatalogueResult> getCatalogue(byte[] encoded) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var decodedBytes = Base64.getDecoder().decode(encoded);
    var combinedId = new String(decodedBytes);
    var catalogueId = CatalogueId.createFrom(combinedId);
    var catalogue = catalogueService.getCatalogueForUser(catalogueId, authentication.getName());
    if (catalogue == null) {
      ResponseEntity.notFound();
    }
    var getCatalogueResult = new GetCatalogueResult()
        .catalogue(catalogue);
    return ResponseEntity.ok(getCatalogueResult);
  }

  @Override
  public ResponseEntity<Void> getInterfaceFileContents(byte[] encodedId, String interfaceName, @Valid String ref) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var decodedBytes = Base64.getDecoder().decode(encodedId);
    var combinedId = new String(decodedBytes);
    var catalogueId = CatalogueId.createFrom(combinedId);
    return null;
  }
}
