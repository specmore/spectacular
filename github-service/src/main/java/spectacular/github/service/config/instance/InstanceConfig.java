package spectacular.github.service.config.instance;

import spectacular.github.service.common.Repository;

public class InstanceConfig {
    private final String installationId;
    private final Repository repository;
    private final InstanceConfigManifest instanceConfigManifest;
    private final String error;

    public InstanceConfig(String installationId, Repository repository, InstanceConfigManifest instanceConfigManifest, String error) {
        this.installationId = installationId;
        this.repository = repository;
        this.instanceConfigManifest = instanceConfigManifest;
        this.error = error;
    }

    public String getInstallationId() {
        return installationId;
    }

    public Repository getRepository() {
        return repository;
    }

    public InstanceConfigManifest getInstanceConfigManifest() {
        return instanceConfigManifest;
    }

    public String getError() {
        return error;
    }
}
