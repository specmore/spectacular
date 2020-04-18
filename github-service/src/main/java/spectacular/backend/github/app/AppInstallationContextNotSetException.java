package spectacular.backend.github.app;

public class AppInstallationContextNotSetException extends RuntimeException  {
    public AppInstallationContextNotSetException() {
        super("No GitHub App Installation Context has been set.");
    }
}
