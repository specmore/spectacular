package spectacular.backend.config.instance;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
