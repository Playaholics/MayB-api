package kr.mayb.dto;

import kr.mayb.data.model.Authority;
import kr.mayb.data.model.Member;
import kr.mayb.enums.AccountStatus;
import kr.mayb.enums.AuthorityName;
import kr.mayb.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class MemberDto {

    private long memberId;

    private String email;

    private String name;

    private String profileUrl;

    private Gender gender;

    private LocalDate birthdate;

    private String occupation;

    private String contact;

    private String introduction;

    private String idealType;

    private AccountStatus status;

    private List<AuthorityName> authorities;

    public static MemberDto of(Member member, String contact) {
        List<AuthorityName> authorityNames = member.getAuthorities()
                .stream()
                .map(Authority::getName)
                .toList();

        return new MemberDto(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getProfileUrl(),
                member.getGender(),
                member.getBirthdate(),
                member.getOccupation(),
                contact,
                member.getIntroduction(),
                member.getIdealType(),
                member.getStatus(),
                authorityNames
        );
    }
}
