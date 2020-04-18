package spectacular.backend.installation;

public class InstallationError {
    private final String error;

    public InstallationError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
