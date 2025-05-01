package kr.mayb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record ProductRegistrationRequest(
        @NotBlank
        String name,

        int originalPrice,

        int salePrice,

        @NotBlank
        String description,

        @NotEmpty
        @Size(min = 1)
        List<String> tags,

        @NotEmpty
        @Size(min = 1)
        List<LocalDateTime> dateTimes,

        @NotEmpty
        @Size(min = 1)
        List<String> genders
) {
}
