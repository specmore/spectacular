package spectacular.github.service.installation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstallationController {
    private final InstallationService installationService;

    public InstallationController(InstallationService installationService) {
        this.installationService = installationService;
    }

    @GetMapping("api/installation")
    public InstallationResponse getCurrentInstallation() {
        var installation = this.installationService.getCurrentInstallation();
        return new InstallationResponse(installation);
    }
}
