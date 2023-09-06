package backend.server.service.security;

import backend.server.service.Literals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class represents the implementation of the AuthenticationEntryPoint interface,
 * which is used to handle the authentication exception in case of unauthorized access to a secured REST API.
 * This is the starting point for all HTTP requests when authentication fails.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * This method is used to handle the authentication exception in case of unauthorized access to a secured REST API.
     * It logs the error message and sends a 401 Unauthorized HTTP status code back to the client.
     *
     * @param request       the current request being processed
     * @param response      the response object that will be sent to the client
     * @param authException the authentication exception that was thrown
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, Literals.UNAUTHORIZED);
    }
}