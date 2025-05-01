package kr.mayb.dto;

import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductDateTime;
import kr.mayb.data.model.ProductGender;
import kr.mayb.data.model.ProductTag;

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

        List<String> productTags,
        List<String> productGenders,
        List<LocalDateTime> productDateTimes,

        long creatorId,
        long lastModifierId
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
                product.getProductGenders().stream().map(ProductGender::getGender).toList(),
                product.getProductDateTimes().stream().map(ProductDateTime::getDateTime).toList(),
                product.getCreatorId(),
                product.getLastModifierId()
        );
    }
}
