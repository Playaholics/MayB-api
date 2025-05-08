package kr.mayb.dto;

import kr.mayb.data.model.Product;
import kr.mayb.enums.ProductStatus;

import java.util.List;

public record ProductDto(
        long productId,

        String name,
        String profileImageUrl,
        String detailImageUrl,
        String description,

        int originalPrice,
        int salePrice,

        List<TagInfo> tags,
        List<GenderPrice> genderPrices,
        List<DateTimeInfo> dateTimes,

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
                product.getProductTags().stream().map(TagInfo::of).toList(),
                product.getProductGenders().stream().map(GenderPrice::of).toList(),
                product.getProductDateTimes().stream().map(DateTimeInfo::of).toList(),
                product.getCreatorId(),
                product.getLastModifierId(),
                product.getStatus()
        );
    }
}
