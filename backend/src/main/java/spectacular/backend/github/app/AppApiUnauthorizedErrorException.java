package spectacular.backend.github.app;

public class AppApiUnauthorizedErrorException extends RuntimeException {
  private final AppApiUnauthorizedError unauthorizedError;

  public AppApiUnauthorizedErrorException(AppApiUnauthorizedError unauthorizedError) {
    super("GitHub App Api call failed with '401 - Unauthorized' and returned message: " + unauthorizedError.getMessage());
    this.unauthorizedError = unauthorizedError;
  }

  public AppApiUnauthorizedError getUnauthorizedError() {
    return unauthorizedError;
  }
}
