package kr.mayb.service;

import jakarta.transaction.Transactional;
import kr.mayb.data.model.Authority;
import kr.mayb.data.model.Member;
import kr.mayb.data.repository.AuthorityRepository;
import kr.mayb.data.repository.MemberRepository;
import kr.mayb.dto.MemberDto;
import kr.mayb.enums.AccountStatus;
import kr.mayb.enums.AuthorityName;
import kr.mayb.error.ResourceNotFoundException;
import kr.mayb.security.AESGCMEncoder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final PasswordEncoder passwordEncoder;
    private final AESGCMEncoder aesgcmEncoder;
    private final MemberRepository memberRepository;
    private final AuthorityRepository authorityRepository;

    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional
    public Member saveMember(Member member) {
        if (StringUtils.isNotBlank(member.getPassword())) {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
        }

        if (StringUtils.isNotBlank(member.getContact())) {
            member.setContact(aesgcmEncoder.encrypt(member.getContact()));
        }

        Authority authority = authorityRepository.findByName(AuthorityName.ROLE_USER);
        member.setAuthorities(Collections.singletonList(authority));
        member.setStatus(AccountStatus.ACTIVE);

        return memberRepository.save(member);
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Optional<Member> findMember(long memberId) {
        return memberRepository.findById(memberId);
    }

    @Transactional
    public MemberDto updateProfile(long memberId, String profileUrl) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found" + memberId));

        member.setProfileUrl(profileUrl);

        Member saved = memberRepository.save(member);
        return convertToMemberDto(saved);
    }

    private MemberDto convertToMemberDto(Member member) {
        String contact = aesgcmEncoder.decrypt(member.getContact());
        return MemberDto.of(member, contact);
    }
}
