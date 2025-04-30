package kr.mayb.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberUpdateRequest(
        @NotBlank
        String name,

        String introduction,

        String idealType
) {
}
