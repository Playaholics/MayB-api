package kr.mayb.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtils {

    public static Optional<HttpServletRequest> getCurrentRequest() {
        return Optional.of(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }
}