package spectacular.github.service.security

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

class JWTCookieToAuthorizationHeaderFilterTest extends Specification {
    def jwtCookieName = "test-cookie"
    def filter = new JWTCookieToAuthorizationHeaderFilter(jwtCookieName)
    def mockFilterChain = new MockFilterChain();
    def mockResponse = new MockHttpServletResponse();

    def "filter extracts the JWT cookie value and assigns it as an authorisation header bearer token"() {
        given: "a test JWT"
        def testToken = "test-value"
        and: "a request with with the JWT sent as a cookie"
        def request = new MockHttpServletRequest()
        def jwtCookie = new Cookie(jwtCookieName, testToken)
        request.setCookies(jwtCookie)

        when: "the request is handled by the filter"
        filter.doFilterInternal(request, mockResponse, mockFilterChain);

        then: "the filtered request is passed to the next filter in the chain"
        def filteredRequest = (HttpServletRequest) mockFilterChain.getRequest();
        and: "the Authorisation header on the filtered request contains JWT cookie's value as a Bearer token"
        def authorisationHeaderValue = filteredRequest.getHeader(HttpHeaders.AUTHORIZATION)
        authorisationHeaderValue == "Bearer ${testToken}"
    }

    def "filter does not override an existing authorisation header with a cookie value"() {
        given: "a request with an authorisation header set"
        def request = new MockHttpServletRequest()
        def authorisationHeaderValue = "some test value"
        request.addHeader(HttpHeaders.AUTHORIZATION, authorisationHeaderValue)
        and: "a different JWT sent as a cookie"
        def testToken = "another-test-value"
        def jwtCookie = new Cookie(jwtCookieName, testToken)
        request.setCookies(jwtCookie)


        when: "the request is handled by the filter"
        filter.doFilterInternal(request, mockResponse, mockFilterChain);

        then: "the filtered request is passed to the next filter in the chain"
        def filteredRequest = (HttpServletRequest) mockFilterChain.getRequest();
        and: "the filtered request's authentication header remains the same as the original request's"
        def resultAuthorisationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION)
        resultAuthorisationHeaderValue == authorisationHeaderValue

    }
}
