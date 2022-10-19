package spectacular.backend.app;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

import java.time.Duration;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import spectacular.backend.api.AppApi;
import spectacular.backend.api.model.AppDetails;
import spectacular.backend.api.model.AppLoginRequest;
import spectacular.backend.api.model.GetInstallationsResult;
import spectacular.backend.api.model.Installation;
import spectacular.backend.api.model.UserDetails;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.github.app.user.AppUserAuthenticationService;

@RestController
public class AppController implements AppApi {
  private final AppUserAuthenticationService appUserAuthenticationService;
  private final UserSessionTokenService userSessionTokenService;
  private final InstallationService installationService;
  private final String jwtCookieName;

  /**
   * Resources related to the GitHub App installation and user sessions.
   * @param appUserAuthenticationService the service responsible for creating user sessions after a successful GitHub OAuth login workflow
   * @param userSessionTokenService the service responsible for generating JWTs
   * @param installationService the service providing installation details for this GitHub integration
   * @param jwtCookieName the config value for the cookie name returned to the user agent
   */
  public AppController(AppUserAuthenticationService appUserAuthenticationService,
                       UserSessionTokenService userSessionTokenService,
                       InstallationService installationService,
                       @Value("${security.authentication.jwt.cookie-name}") String jwtCookieName) {
    this.appUserAuthenticationService = appUserAuthenticationService;
    this.userSessionTokenService = userSessionTokenService;
    this.installationService = installationService;
    this.jwtCookieName = jwtCookieName;
  }

  @Override
  public ResponseEntity<UserDetails> createUserSession(@Valid AppLoginRequest appLoginRequest) {
    final var createUserSessionResult = this.appUserAuthenticationService.createUserSession(appLoginRequest.getUserCode());

    if (createUserSessionResult.getUserSessionToken() == null) {
      throw new RuntimeException("An error occurred while generating the user session.");
    }

    final var cookie = ResponseCookie.from(jwtCookieName, createUserSessionResult.getUserSessionToken())
        .httpOnly(true)
        .secure(true)
        .path("/")
        .sameSite("Strict")
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(createUserSessionResult.getUserDetails());
  }

  @Override
  public ResponseEntity<Void> deleteUserSession() {
    final var cookie = ResponseCookie.from(jwtCookieName, null)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .sameSite("Strict")
        .maxAge(Duration.ZERO)
        .build();

    return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .build();
  }

  @Override
  public ResponseEntity<AppDetails> getAppDetails() {
    final var clientId = this.appUserAuthenticationService.getClientId();
    final var appDetails = new AppDetails().clientId(clientId);
    return ok(appDetails);
  }

  @Override
  public ResponseEntity<Installation> getInstallation(Integer installationId) {
    final var securityPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (securityPrincipal instanceof Jwt) {
      final var jwt = (Jwt) securityPrincipal;
      final var installationIds = this.userSessionTokenService.getInstallationIds(jwt.getTokenValue());
      if (installationIds.stream().anyMatch(userInstallationId -> userInstallationId.intValue() == installationId)) {
        final var installation = this.installationService.getInstallation(installationId);
        return ok(installation);
      }

      return notFound().build();
    }
    throw new RuntimeException("An error occurred while processing the user session.");
  }

  @Override
  public ResponseEntity<GetInstallationsResult> getInstallations(@Valid byte[] catalogueEncodedId) {
    final var securityPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (securityPrincipal instanceof Jwt) {
      final var jwt = (Jwt) securityPrincipal;
      final var installationIds = this.userSessionTokenService.getInstallationIds(jwt.getTokenValue());

      Optional<CatalogueId> catalogueQuery = Optional.empty();
      if (catalogueEncodedId != null && catalogueEncodedId.length > 0) {
        catalogueQuery = Optional.of(CatalogueId.createFromBase64(catalogueEncodedId));
      }

      final var getInstallationsResult = this.installationService.getInstallations(installationIds, catalogueQuery);
      return ok(getInstallationsResult);
    }
    throw new RuntimeException("An error occurred while processing the user session.");
  }

  @Override
  public ResponseEntity<UserDetails> getUserDetails() {
    final var securityPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (securityPrincipal instanceof Jwt) {
      final var jwt = (Jwt) securityPrincipal;
      var userDetails = this.userSessionTokenService.populateUserDetailsFromSessionToken(jwt.getTokenValue());
      return ok(userDetails);
    }
    throw new RuntimeException("An error occurred while processing the user session.");
  }
}
