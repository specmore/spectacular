package spectacular.backend.catalogues;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import spectacular.backend.api.CataloguesApi;
import spectacular.backend.api.model.FindCataloguesResult;
import spectacular.backend.api.model.GetCatalogueResult;
import spectacular.backend.api.model.GetInterfaceResult;
import spectacular.backend.app.UserSessionTokenService;
import spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError;
import spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemErrorType;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.installation.InstallationService;

@RestController
public class CataloguesController implements CataloguesApi {
  private final CatalogueService catalogueService;
  private final UserSessionTokenService userSessionTokenService;
  private final InstallationService installationService;

  public CataloguesController(CatalogueService catalogueService, UserSessionTokenService userSessionTokenService,
                              InstallationService installationService) {
    this.catalogueService = catalogueService;
    this.userSessionTokenService = userSessionTokenService;
    this.installationService = installationService;
  }

  @Override
  public ResponseEntity<FindCataloguesResult> findCataloguesForUser(Integer installationId) {
    final var jwt = validateRequest(installationId);

    final var installation = this.installationService.getInstallation(installationId);
    final var catalogues = catalogueService.findCataloguesForOrgAndUser(installation.getOwner(), jwt.getSubject());
    final var findCataloguesResult = new FindCataloguesResult().catalogues(catalogues);
    return ok(findCataloguesResult);
  }

  @Override
  public ResponseEntity<GetCatalogueResult> getCatalogue(Integer installationId, byte[] encoded) {
    final var jwt = validateRequest(installationId);

    var decodedBytes = Base64.getDecoder().decode(encoded);
    var combinedId = new String(decodedBytes);
    var catalogueId = CatalogueId.createFrom(combinedId);

    var getCatalogueForUserResult = catalogueService.getCatalogueForUser(catalogueId, jwt.getSubject());

    handleAnyError(getCatalogueForUserResult.getError());

    var getCatalogueResult = new GetCatalogueResult().catalogue(getCatalogueForUserResult.getCatalogueDetails());
    return ResponseEntity.ok(getCatalogueResult);
  }

  @Override
  public ResponseEntity<GetInterfaceResult> getInterfaceDetails(Integer installationId, byte[] encodedId, String interfaceName) {
    final var jwt = validateRequest(installationId);

    var decodedBytes = Base64.getDecoder().decode(encodedId);
    var combinedId = new String(decodedBytes);
    var catalogueId = CatalogueId.createFrom(combinedId);

    var getInterfaceDetailsResult = this.catalogueService.getInterfaceDetails(catalogueId, interfaceName, jwt.getSubject());

    handleAnyError(getInterfaceDetailsResult.getError());

    return ResponseEntity.ok(getInterfaceDetailsResult.getGetInterfaceResult());
  }

  @Override
  public ResponseEntity<Object> getInterfaceFileContents(Integer installationId, byte[] encodedId, String interfaceName, @Valid String ref) {
    final var jwt = validateRequest(installationId);

    var decodedBytes = Base64.getDecoder().decode(encodedId);
    var combinedId = new String(decodedBytes);
    var catalogueId = CatalogueId.createFrom(combinedId);

    try {
      var getInterfaceFileContentsResult = this.catalogueService.getInterfaceFileContents(
          catalogueId,
          interfaceName,
          ref,
          jwt.getSubject());

      handleAnyError(getInterfaceFileContentsResult.getError());

      return ResponseEntity
          .ok()
          .contentType(getInterfaceFileContentsResult.getInterfaceFileContents().getMediaTypeGuess())
          .body(getInterfaceFileContentsResult.getInterfaceFileContents().getContents());
    } catch (UnsupportedEncodingException e) {
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred while decoding the file contents.");
    }
  }

  private Jwt validateRequest(Integer installationId) {
    final var securityPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!(securityPrincipal instanceof Jwt)) {
      throw new RuntimeException("An error occurred while processing the user session.");
    }

    final var jwt = (Jwt) securityPrincipal;
    final var installationIds = this.userSessionTokenService.getInstallationIds(jwt.getTokenValue());
    if (installationIds.stream().noneMatch(userInstallationId -> userInstallationId.intValue() == installationId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Installation Id not found.");
    }

    return jwt;
  }

  private void handleAnyError(ConfigurationItemError configurationItemError) {
    if (configurationItemError != null) {
      var errorType = configurationItemError.getType();
      var errorMessage = configurationItemError.getMessage();
      if (errorType == ConfigurationItemErrorType.NOT_FOUND) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
      } else if (errorType == ConfigurationItemErrorType.CONFIG_ERROR) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
      }
    }
  }
}
