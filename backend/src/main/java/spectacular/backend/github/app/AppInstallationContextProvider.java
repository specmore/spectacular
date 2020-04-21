package spectacular.backend.github.app;

public class AppInstallationContextProvider {
  private String installationId = null;

  public String getInstallationId() {
      if (installationId == null) {
          throw new AppInstallationContextNotSetException();
      }
    return installationId;
  }

  public void setInstallationId(String installationId) {
    this.installationId = installationId;
  }
}
