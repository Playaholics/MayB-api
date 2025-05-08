package kr.mayb.dto;

import jakarta.validation.constraints.NotBlank;
import kr.mayb.data.model.ProductGender;

public record GenderPrice(
        @NotBlank
        String gender,
        int price
) {
    public static GenderPrice of(ProductGender gender) {
        return new GenderPrice(gender.getGender(), gender.getPrice());
    }
}
