package spectacular.backend.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.HttpHeaders;

public class AuthorizationHeaderOverrideRequestWrapper extends HttpServletRequestWrapper {
  private final String overrideValue;

  /**
   * Constructs a request object wrapping the given request.
   *
   * @param request       The request to wrap
   * @param overrideValue
   * @throws IllegalArgumentException if the request is null
   */
  public AuthorizationHeaderOverrideRequestWrapper(HttpServletRequest request,
                                                   String overrideValue) {
    super(request);
    this.overrideValue = overrideValue;
  }

  @Override
  public String getHeader(String name) {
    if (name == HttpHeaders.AUTHORIZATION) {
      return overrideValue;
    }
    return super.getHeader(name);
  }
}
