package kr.mayb.security.jwt;

import kr.mayb.data.model.Authority;
import kr.mayb.data.model.Member;
import kr.mayb.data.repository.MemberRepository;
import kr.mayb.dto.MemberDto;
import kr.mayb.error.ResourceNotFoundException;
import kr.mayb.security.AESGCMEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthenticationHelper {

    private final AESGCMEncoder aesgcmEncoder;
    private final MemberRepository memberRepository;

    public JwtUser loadJwtUser(long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        return convertToJwtUser(member);
    }

    public JwtUser convertToJwtUser(Member member) {
        List<SimpleGrantedAuthority> authorities = member.getAuthorities()
                .stream()
                .map(Authority::getName)
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        JwtUser jwtUser = new JwtUser(authorities);
        jwtUser.setId(member.getId());
        jwtUser.setMember(MemberDto.of(member, aesgcmEncoder.decrypt(member.getContact())));

        return jwtUser;
    }

}
