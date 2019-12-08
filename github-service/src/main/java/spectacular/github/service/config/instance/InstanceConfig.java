package spectacular.github.service.config.instance;

import spectacular.github.service.github.domain.Repository;

public class InstanceConfig {
    private final String installationId;
    private final Repository repository;
    private final InstanceConfigManifest instanceConfigManifest;

    public InstanceConfig(String installationId, Repository repository, InstanceConfigManifest instanceConfigManifest) {
        this.installationId = installationId;
        this.repository = repository;
        this.instanceConfigManifest = instanceConfigManifest;
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
}
