package kr.mayb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
        long productId,

        long orderId,

        @Size(min = 1, max = 1000)
        @NotBlank
        String content,

        int starRating
) {
}
