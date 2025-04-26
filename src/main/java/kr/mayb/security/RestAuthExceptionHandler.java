package kr.mayb.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;

@Component
public class RestAuthExceptionHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        request.setAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE, authException);
        response.sendError(HttpStatus.UNAUTHORIZED.value(), authException.getMessage());
    }

}