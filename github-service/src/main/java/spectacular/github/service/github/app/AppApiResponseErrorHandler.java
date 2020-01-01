package spectacular.github.service.github.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

@Component
public class AppApiResponseErrorHandler extends DefaultResponseErrorHandler {
    private final MappingJackson2HttpMessageConverter messageConverter;

    public AppApiResponseErrorHandler(MappingJackson2HttpMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    @Override
    protected void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
        if (statusCode == HttpStatus.UNAUTHORIZED) {
            AppApiUnauthorizedError unauthorizedError = (AppApiUnauthorizedError) messageConverter.read(AppApiUnauthorizedError.class, response);
            throw new AppApiUnauthorizedErrorException(unauthorizedError);
        }
        super.handleError(response, statusCode);
    }
}
