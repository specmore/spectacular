package spectacular.github.service.config.instance;

import java.util.List;

public class InstancesResponse {
    private final List<InstanceConfig> instances;

    public InstancesResponse(List<InstanceConfig> instances) {
        this.instances = instances;
    }

    public List<InstanceConfig> getInstances() {
        return instances;
    }
}
