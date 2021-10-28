package spectacular.backend.app;

import static org.springframework.http.ResponseEntity.ok;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import spectacular.backend.api.AppApi;
import spectacular.backend.api.model.AppDetails;
import spectacular.backend.api.model.AppLoginRequest;
import spectacular.backend.api.model.UserDetails;
import spectacular.backend.github.app.user.AppUserAuthenticationService;

@RestController
public class AppController implements AppApi {
  private final AppUserAuthenticationService appUserAuthenticationService;
  private final String jwtCookieName;

  public AppController(AppUserAuthenticationService appUserAuthenticationService,
                       @Value("${security.authentication.jwt.cookie-name}") String jwtCookieName) {
    this.appUserAuthenticationService = appUserAuthenticationService;
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
        .path("/")
        .sameSite("Strict")
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(createUserSessionResult.getUserDetails());
  }

  @Override
  public ResponseEntity<AppDetails> getAppDetails() {
    final var clientId = this.appUserAuthenticationService.getClientId();
    final var appDetails = new AppDetails().clientId(clientId);
    return ok(appDetails);
  }
}
