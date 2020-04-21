package spectacular.backend.installation;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;


import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import spectacular.backend.github.app.AppInstallationContextProvider;

@Component
public class InstallationIdInterceptor extends HandlerInterceptorAdapter {
  private static final String INSTALLATION_ID_HEADER_NAME = "x-spec-installation-id";

  private final AppInstallationContextProvider appInstallationContextProvider;

  public InstallationIdInterceptor(AppInstallationContextProvider appInstallationContextProvider) {
    this.appInstallationContextProvider = appInstallationContextProvider;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    var installationIdHeaderValue = request.getHeader(INSTALLATION_ID_HEADER_NAME);

    if (installationIdHeaderValue == null) {
      var mapper = new ObjectMapper();
      var errorMessage = new InstallationError(
          "No '" + INSTALLATION_ID_HEADER_NAME + "' http request header was found.");

      response.setStatus(SC_BAD_REQUEST);
      response.setContentType(APPLICATION_JSON.toString());
      response.getWriter().write(mapper.writeValueAsString(errorMessage));
      return false;
    }

    this.appInstallationContextProvider.setInstallationId(installationIdHeaderValue);
    return true;
  }
}
