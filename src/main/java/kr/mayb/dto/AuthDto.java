package kr.mayb.dto;

import kr.mayb.security.TokenDto;

public record AuthDto(
        MemberDto member,
        TokenDto accessToken,
        TokenDto refreshToken
) {
}
