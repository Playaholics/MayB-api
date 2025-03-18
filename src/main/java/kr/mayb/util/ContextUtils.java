package kr.mayb.util;

import kr.mayb.dto.MemberDto;
import kr.mayb.security.jwt.JwtUser;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class ContextUtils {

    public static Optional<JwtUser> getCurrentJwtUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(JwtUser.class::isInstance)
                .map(JwtUser.class::cast);
    }

    public static Optional<MemberDto> getCurrentMember() {
        return getCurrentJwtUser()
                .map(JwtUser::getMember);
    }

    public static MemberDto loadMember() {
        return ContextUtils.getCurrentMember()
                .orElseThrow(() -> new UsernameNotFoundException("Cannot load current user context."));
    }
}