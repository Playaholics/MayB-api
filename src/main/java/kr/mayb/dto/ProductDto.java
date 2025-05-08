package kr.mayb.dto;

import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductDateTime;
import kr.mayb.data.model.ProductTag;
import kr.mayb.enums.ProductStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ProductDto(
        long productId,

        String name,
        String profileImageUrl,
        String detailImageUrl,
        String description,

        int originalPrice,
        int salePrice,

        List<String> tags,
        List<GenderPrice> genderPrices,
        List<LocalDateTime> dateTimes,

        long creatorId,
        long lastModifierId,

        ProductStatus status
) {
    public static ProductDto of(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getProfileImageUrl(),
                product.getDetailImageUrl(),
                product.getDescription(),
                product.getOriginalPrice(),
                product.getSalePrice(),
                product.getProductTags().stream().map(ProductTag::getName).toList(),
                product.getProductGenders().stream().map(GenderPrice::of).toList(),
                product.getProductDateTimes().stream().map(ProductDateTime::getDateTime).toList(),
                product.getCreatorId(),
                product.getLastModifierId(),
                product.getStatus()
        );
    }
}
