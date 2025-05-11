package kr.mayb.dto;

import kr.mayb.data.model.Product;

public record ProductSimple(
        long productId,
        String name
) {
    public static ProductSimple of(Product product) {
        return new ProductSimple(product.getId(), product.getName());
    }
}
