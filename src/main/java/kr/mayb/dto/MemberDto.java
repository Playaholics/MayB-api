package kr.mayb.dto;

import kr.mayb.data.model.Member;
import kr.mayb.enums.Gender;
import kr.mayb.security.AESGCMEncoder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MemberDto {

    private long memberId;

    private String email;

    private String name;

    private Gender gender;

    private LocalDate birthdate;

    private String occupation;

    private String contact;

    private String introduction;

    private String idealType;

    public static MemberDto of(Member member, String contact) {
        return new MemberDto(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getGender(),
                member.getBirthdate(),
                member.getOccupation(),
                contact,
                member.getIntroduction(),
                member.getIdealType()
        );
    }
}
