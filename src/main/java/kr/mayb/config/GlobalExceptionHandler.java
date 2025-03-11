package kr.mayb.config;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import kr.mayb.util.ErrorUtils;
import kr.mayb.util.response.ApiResponse;
import kr.mayb.util.response.Error;
import kr.mayb.util.response.Responses;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements ErrorController {
    private static final String ERROR_PATH = "/error";

    @RequestMapping(ERROR_PATH)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> error(HttpServletRequest request) {
        HttpStatus status = ErrorUtils.extractStatus(request);
        Throwable throwable = ErrorUtils.extractError(request);
        String path = ErrorUtils.extractPath(request);

        if (throwable != null) {
            logFallbackError(status, throwable.getMessage(), path, throwable);

            Error error = Error.of(path, status, throwable);
            return Responses.error(error);

        } else {
            String message = ErrorUtils.extractErrorMessage(status, request);
            logFallbackError(status, message, path, null);

            Error error = Error.of(path, status, message);
            return Responses.error(error);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleExceptions(HttpServletRequest request, Exception ex) {
        HttpStatus status = ErrorUtils.extractHttpStatus(ex);

        ErrorUtils.logError(status, request, ex);
        Error error = Error.of(request.getRequestURI(), status, ex);

        return Responses.error(error);
    }

    @ExceptionHandler({MultipartException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Void>> handleMultipartException(HttpServletRequest request, Exception ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Error error = Error.of(request.getRequestURI(), status, ex);
        return Responses.error(error);
    }

    @ExceptionHandler({AuthenticationException.class, AccessDeniedException.class})
    public ResponseEntity<ApiResponse<Void>> handleAuthException(HttpServletRequest request, Exception ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErrorUtils.logError(status, request, ex);

        Error error = Error.of(request.getRequestURI(), status, ex);
        return Responses.error(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(HttpServletRequest request, Exception ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorUtils.logError(status, request, ex);
        Error error = Error.of(request.getRequestURI(), status, ex);
        return Responses.error(error);
    }

    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortException(HttpServletRequest request, Exception ex) {
        // client cannot receive a response. just log the request.
        ErrorUtils.logError(HttpStatus.I_AM_A_TEAPOT, request, ex);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if (request instanceof ServletWebRequest) {
            HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();

            ErrorUtils.logError(statusCode, servletRequest, ex);

            Error error = Error.of(servletRequest.getRequestURI(), statusCode, ex);
            ApiResponse<Void> root = ApiResponse.error(error);
            return ResponseEntity.status(statusCode).headers(headers).body(root);
        }

        ErrorUtils.logError(statusCode, null, ex);

        Error error = Error.of(null, statusCode, ex);
        ApiResponse<Void> root = ApiResponse.error(error);
        return ResponseEntity.status(statusCode).headers(headers).body(root);
    }

    private void logFallbackError(HttpStatus status, String message, String path, Throwable error) {
        if (!status.is5xxServerError()) {
            return;
        }
        log.error("Resolved fallback exception:[ {} ] | Request URI:[ {} ]", message, path, error);
    }
}
