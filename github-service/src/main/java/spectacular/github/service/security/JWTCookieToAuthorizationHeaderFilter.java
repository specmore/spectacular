package spectacular.github.service.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTCookieToAuthorizationHeaderFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JWTCookieToAuthorizationHeaderFilter.class);

    private final String jwtCookieName;

    public JWTCookieToAuthorizationHeaderFilter(String jwtCookieName) {
        this.jwtCookieName = jwtCookieName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = request;
        Cookie jwtCookie = WebUtils.getCookie(httpRequest, jwtCookieName);
        String authorisationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (jwtCookie == null) logger.debug("JWT cookie not found with name: " + jwtCookieName);
        if (jwtCookie != null) logger.debug("Found JWT cookie.");
        if (authorisationHeader == null) logger.debug("No authorisation header found.");
        if (authorisationHeader != null) logger.debug("An authorisation header already set.");

        if (jwtCookie != null && authorisationHeader == null) {
            var newAuthorisationHeader = "Bearer " + jwtCookie.getValue();
            var overriddenRequestWrapper = new AuthorizationHeaderOverrideRequestWrapper(request, newAuthorisationHeader);
            logger.debug("Continuing chain with an overridden authorisation request header");
            filterChain.doFilter(overriddenRequestWrapper, response);
            return;
        }

        logger.debug("Continuing chain with no action");
        filterChain.doFilter(request, response);
    }
}
