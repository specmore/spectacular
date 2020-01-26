package spectacular.github.service.config.instance;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import spectacular.github.service.github.app.AppInstallationContextProvider;

@RestController
public class InstanceConfigController {

    private final InstanceConfigService instanceConfigService;
    private final AppInstallationContextProvider appInstallationContextProvider;

    public InstanceConfigController(InstanceConfigService instanceConfigService, AppInstallationContextProvider appInstallationContextProvider) {
        this.instanceConfigService = instanceConfigService;
        this.appInstallationContextProvider = appInstallationContextProvider;
    }

    @GetMapping("api/{installationId}/instances")
    public InstancesResponse getInstances(@PathVariable("installationId") String installationId, Authentication authentication) {
        //todo: move installationId to a header and set context using a interceptor
        appInstallationContextProvider.setInstallationId(installationId);

        var instanceConfigs = this.instanceConfigService.getInstanceConfigsForInstallation();
        return new InstancesResponse(instanceConfigs);
    }
}
