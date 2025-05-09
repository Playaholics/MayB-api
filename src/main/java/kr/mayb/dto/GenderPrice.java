package kr.mayb.dto;

import jakarta.validation.constraints.NotBlank;
import kr.mayb.data.model.ProductGenderPrice;

public record GenderPrice(
        long id,
        @NotBlank
        String gender,
        int price
) {
    public static GenderPrice of(ProductGenderPrice gender) {
        return new GenderPrice(gender.getId(), gender.getGender(), gender.getPrice());
    }
}
