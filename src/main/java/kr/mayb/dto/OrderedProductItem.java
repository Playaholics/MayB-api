package kr.mayb.dto;

import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductGenderPrice;
import kr.mayb.data.model.ProductSchedule;

public record OrderedProductItem(
        Product product,
        ProductGenderPrice genderPrice,
        ProductSchedule schedule
) {
    public static OrderedProductItem of(Product product, ProductGenderPrice genderPrice, ProductSchedule schedule) {
        return new OrderedProductItem(product, genderPrice, schedule);
    }
}
