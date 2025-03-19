package kr.mayb.service;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import kr.mayb.data.model.Member;
import kr.mayb.dto.AuthDto;
import kr.mayb.dto.MemberDto;
import kr.mayb.dto.MemberSignupRequest;
import kr.mayb.error.BadRequestException;
import kr.mayb.security.AESGCMEncoder;
import kr.mayb.security.TokenDto;
import kr.mayb.security.jwt.TokenHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final TokenHelper tokenHelper;
    private final AESGCMEncoder aesgcmEncoder;
    private final PasswordEncoder passwordEncoder;

    public AuthDto registerMember(MemberSignupRequest request) {
        if (isSignedUp(request.email())) {
            throw new BadRequestException("Already signed up email");
        }

        Member saved = memberService.saveMember(request.toEntity());
        return login(saved);
    }

    public AuthDto login(String email, String password) {
        if (StringUtils.isAnyBlank(email, password)) {
            throw new AuthenticationServiceException("Authentication failed. No email or password was provided.");
        }

        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Authentication failed. Member not found: " + email));

        if (StringUtils.isBlank(member.getPassword())) {
            throw new BadCredentialsException("Authentication failed. Oauth member didn't set password");
        }

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BadCredentialsException("Authentication failed. Invalid password");
        }

        return login(member);
    }

    @Transactional
    private AuthDto login(Member member) {
        String contact = aesgcmEncoder.decrypt(member.getContact());
        MemberDto memberDto = MemberDto.of(member, contact);
        TokenDto accessToken = tokenHelper.generateAccessToken(member);
        TokenDto refreshToken = tokenHelper.generateRefreshToken(member);

        return new AuthDto(memberDto, accessToken, refreshToken);
    }

    private boolean isSignedUp(String email) {
        return memberService.existsByEmail(email);
    }

    @Transactional
    public AuthDto refresh(String refreshToken) {
        Claims claims = tokenHelper.getRefreshClaims(refreshToken);
        Integer memberId = claims.get("id", Integer.class);
        Member member = memberService.findMember(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Authentication failed. Member not found: " + memberId));

        tokenHelper.removeOldRefreshToken(member.getId(), refreshToken);
        return login(member);
    }
}
