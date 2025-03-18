package kr.mayb.service;

import kr.mayb.data.model.Member;
import kr.mayb.dto.AuthDto;
import kr.mayb.dto.MemberDto;
import kr.mayb.dto.MemberSignupRequest;
import kr.mayb.error.BadRequestException;
import kr.mayb.security.AESGCMEncoder;
import kr.mayb.security.TokenDto;
import kr.mayb.security.jwt.TokenHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final TokenHelper tokenHelper;
    private final AESGCMEncoder aesgcmEncoder;

    public AuthDto registerMember(MemberSignupRequest request) {
        if (isSignUpped(request.email())) {
            throw new BadRequestException("이미 가입된 이메일 입니다.");
        }

        Member saved = memberService.saveMember(request.toEntity());
        return login(saved);
    }

    private AuthDto login(Member member) {
        String contact = aesgcmEncoder.decrypt(member.getContact());
        MemberDto memberDto = MemberDto.of(member, contact);
        TokenDto accessToken = tokenHelper.generateAccessToken(member);
        TokenDto refreshToken = tokenHelper.generateRefreshToken(member);

        return new AuthDto(memberDto, accessToken, refreshToken);
    }

    private boolean isSignUpped(String email) {
        return memberService.existsByEmail(email);
    }
}
