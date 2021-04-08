package spectacular.backend.catalogues;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import spectacular.backend.api.CataloguesApi;
import spectacular.backend.api.model.FindCataloguesResult;
import spectacular.backend.api.model.GetCatalogueResult;
import spectacular.backend.api.model.GetInterfaceResult;
import spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemError;
import spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemErrorType;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.interfaces.InterfaceService;

@RestController
public class CataloguesController implements CataloguesApi {
  private final CatalogueService catalogueService;
  private final InterfaceService interfaceService;

  public CataloguesController(CatalogueService catalogueService, InterfaceService interfaceService) {
    this.catalogueService = catalogueService;
    this.interfaceService = interfaceService;
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

    var getCatalogueForUserResult = catalogueService.getCatalogueForUser(catalogueId, authentication.getName());

    handleAnyError(getCatalogueForUserResult.getError());

    var getCatalogueResult = new GetCatalogueResult().catalogue(getCatalogueForUserResult.getCatalogueDetails());
    return ResponseEntity.ok(getCatalogueResult);
  }

  @Override
  public ResponseEntity<GetInterfaceResult> getInterfaceDetails(byte[] encodedId, String interfaceName) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var decodedBytes = Base64.getDecoder().decode(encodedId);
    var combinedId = new String(decodedBytes);
    var catalogueId = CatalogueId.createFrom(combinedId);

    var getInterfaceDetailsResult = this.catalogueService.getInterfaceDetails(catalogueId, interfaceName, authentication.getName());

    handleAnyError(getInterfaceDetailsResult.getError());

    return ResponseEntity.ok(getInterfaceDetailsResult.getGetInterfaceResult());
  }

  @Override
  public ResponseEntity<Object> getInterfaceFileContents(byte[] encodedId, String interfaceName, @Valid String ref) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var decodedBytes = Base64.getDecoder().decode(encodedId);
    var combinedId = new String(decodedBytes);
    var catalogueId = CatalogueId.createFrom(combinedId);

    try {
      var getInterfaceFileContentsResult = this.catalogueService.getInterfaceFileContents(
          catalogueId,
          interfaceName,
          ref,
          authentication.getName());

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

  private void handleAnyError(GetCatalogueManifestConfigurationItemError configurationItemError) {
    if (configurationItemError != null) {
      var errorType = configurationItemError.getType();
      var errorMessage = configurationItemError.getMessage();
      if (errorType == GetCatalogueManifestConfigurationItemErrorType.NOT_FOUND) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
      } else if (errorType == GetCatalogueManifestConfigurationItemErrorType.CONFIG_ERROR) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
      }
    }
  }
}
