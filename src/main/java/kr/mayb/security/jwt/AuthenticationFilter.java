package kr.mayb.security.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final TokenHelper tokenHelper;
    private final AuthenticationHelper authenticationHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // get access token claims
            Optional<Claims> claimsOptional = tokenHelper.getAccessClaims();

            if (claimsOptional.isPresent()) {
                Claims claims = claimsOptional.get();
                Integer memberId = claims.get("id", Integer.class);
                JwtUser user = authenticationHelper.loadJwtUser(memberId);

                SecurityContextHolder.getContext().setAuthentication(user);
            }

        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
