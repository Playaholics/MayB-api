package kr.mayb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.mayb.data.model.Member;
import kr.mayb.enums.Gender;

import java.time.LocalDate;

public record MemberSignupRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8, max = 30)
        String password,

        @NotBlank
        String name,

        @NotNull
        Gender gender,

        @NotNull
        int birthYear,

        @NotNull
        int birthMonth,

        @NotNull
        int birthDay,

        @NotBlank
        String occupation,

        @NotBlank
        String contact,

        String introduction,

        String idealType
) {
        public Member toEntity() {
                Member member = new Member();
                member.setEmail(this.email);
                member.setPassword(this.password);
                member.setName(this.name);
                member.setGender(this.gender);
                member.setBirthdate(mergeBirthdate());
                member.setOccupation(this.occupation);
                member.setContact(trimContact());
                member.setIntroduction(this.introduction);
                member.setIdealType(this.idealType);

                return member;
        }

        private LocalDate mergeBirthdate() {
                return LocalDate.of(this.birthYear, this.birthMonth, this.birthDay);
        }

        private String trimContact() {
                return String.join("", this.contact.split("-"));
        }
}
