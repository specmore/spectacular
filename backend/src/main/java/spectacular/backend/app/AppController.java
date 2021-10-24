package spectacular.backend.app;

import static org.springframework.http.ResponseEntity.ok;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import spectacular.backend.api.AppApi;
import spectacular.backend.api.model.AppDetails;
import spectacular.backend.github.app.AppUserAuthenticationService;

@RestController
public class AppController implements AppApi {
  private final AppUserAuthenticationService appUserAuthenticationService;

  public AppController(AppUserAuthenticationService appUserAuthenticationService) {
    this.appUserAuthenticationService = appUserAuthenticationService;
  }

  @Override
  public ResponseEntity<AppDetails> getAppDetails() {
    final var clientId = this.appUserAuthenticationService.getClientId();
    final var appDetails = new AppDetails().clientId(clientId);
    return ok(appDetails);
  }
}
