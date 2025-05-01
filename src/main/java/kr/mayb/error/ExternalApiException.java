package kr.mayb.error;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class ExternalApiException extends RuntimeException {
    public ExternalApiException() {
        this("External API Exception");
    }

    public ExternalApiException(String message) {
        super(message);
    }
}
