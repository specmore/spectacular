package spectacular.backend.installation;

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

  @GetMapping("installation")
  public InstallationResponse getCurrentInstallation() {
    var installation = this.installationService.getCurrentInstallation();
    return new InstallationResponse(installation);
  }

  @Override
  public ResponseEntity<Installation> getInstallation() {
    return null;
  }
}
