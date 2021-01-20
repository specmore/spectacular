package spectacular.backend.installation;

import static org.springframework.http.ResponseEntity.ok;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import spectacular.backend.api.InstallationApi;
import spectacular.backend.api.model.Installation;

@RestController
public class InstallationController implements InstallationApi {
  private final InstallationService installationService;

  public InstallationController(InstallationService installationService) {
    this.installationService = installationService;
  }

  @Override
  public ResponseEntity<Installation> getInstallation() {
    var installation = installationService.getCurrentInstallation();
    return ok(installation);
  }
}
