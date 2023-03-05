package spectacular.backend.github.app.user;

public class OAuthUserAccessTokenErrorException extends RuntimeException {
  private final String code;

  /**
   * A custom runtime exception for errors that happen during the GitHub App OAuth Login process.
   * @param code the OAuth code received back from GitHub at the end of the OAuth workflow
   */
  public OAuthUserAccessTokenErrorException(String code) {
    super("An error occurred while completing a GitHub user login. " +
        "The application's GitHub OAuth client secret may not yet be configured correctly or the OAuth code provided ('" +
        code + "') may have expired or be fraudulent.");
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
