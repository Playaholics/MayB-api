package kr.mayb.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record ProductUpdateRequest(
        @NotBlank
        String name,

        int originalPrice,

        int salePrice,

        @NotBlank
        String description,

        @NotNull
        @Size(min = 1)
        List<String> tags,

        @NotNull
        @Size(min = 1)
        List<LocalDateTime> schedules,

        @Valid
        @NotNull
        List<GenderPrice> genderPrices
) {
}
