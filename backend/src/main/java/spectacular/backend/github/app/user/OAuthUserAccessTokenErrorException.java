package spectacular.backend.github.app.user;

public class OAuthUserAccessTokenErrorException extends RuntimeException {
  private final String code;

  /**
   * A custom runtime exception for errors that happen during the GitHub App OAuth Login process.
   * @param code the OAuth code received back from GitHub at the end of the OAuth workflow
   */
  public OAuthUserAccessTokenErrorException(String code) {
    super("GitHub AppOAuthApiClient requestUserAccessToken call failed to retrieve an user access token for OAuth code: " + code +
        ". Check if the client secret is correct or maybe a fraudulent code was used.");
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
