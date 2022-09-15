package spectacular.backend.installation;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import spectacular.backend.github.app.AppInstallationContextProvider;

@Component
public class InstallationIdInterceptor extends HandlerInterceptorAdapter {
  private static final String INSTALLATION_ID_PATH_PARAMETER = "installationId";

  private final AppInstallationContextProvider appInstallationContextProvider;

  public InstallationIdInterceptor(AppInstallationContextProvider appInstallationContextProvider) {
    this.appInstallationContextProvider = appInstallationContextProvider;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

    String installationId = pathVariables.get(INSTALLATION_ID_PATH_PARAMETER);

    if (installationId != null) {
      this.appInstallationContextProvider.setInstallationId(installationId);
    }

    return true;
  }
}
