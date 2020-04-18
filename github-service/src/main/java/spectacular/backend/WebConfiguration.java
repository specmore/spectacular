package spectacular.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import spectacular.backend.installation.InstallationIdInterceptor;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Autowired
    InstallationIdInterceptor installationIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(installationIdInterceptor);
    }
}
