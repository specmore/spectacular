package spectacular.github.service.security;

import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class AuthorizationHeaderOverrideRequestWrapper extends HttpServletRequestWrapper {
    private final String overrideValue;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @param overrideValue
     * @throws IllegalArgumentException if the request is null
     */
    public AuthorizationHeaderOverrideRequestWrapper(HttpServletRequest request, String overrideValue) {
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
