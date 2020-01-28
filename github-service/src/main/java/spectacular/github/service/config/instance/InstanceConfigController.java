package spectacular.github.service.config.instance;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import spectacular.github.service.github.app.AppInstallationContextProvider;

@RestController
public class InstanceConfigController {

    private final InstanceConfigService instanceConfigService;

    public InstanceConfigController(InstanceConfigService instanceConfigService) {
        this.instanceConfigService = instanceConfigService;
    }

    @GetMapping("api/instances")
    public InstancesResponse getInstances(JwtAuthenticationToken authToken) {
        var instanceConfigs = this.instanceConfigService.getInstanceConfigsForUser(authToken.getName());
        return new InstancesResponse(instanceConfigs);
    }
}
