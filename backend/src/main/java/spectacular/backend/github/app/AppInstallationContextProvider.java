package spectacular.backend.github.app;

/**
 * A bean for holding the GitHub App Installation Id context sent with each API request.
 */
public class AppInstallationContextProvider {
  private String installationId = null;

  /**
   * Get the installation if for the current API request context.
   *
   * @return the installation id for the API request currently being handled
   */
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
