package kr.mayb.dto;

import kr.mayb.data.model.Product;
import kr.mayb.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProductDto {
    private final long productId;

    private final String name;
    private final String profileImageUrl;
    private final String detailImageUrl;
    private final String description;

    private final int originalPrice;
    private final int salePrice;

    private final List<TagInfo> tags;
    private final List<GenderPrice> genderPrices;
    private final List<ScheduleInfo> schedules;

    private final ProductStatus status;

    private Long creatorId;
    private Long lastModifierId;

    public ProductDto(Product product, boolean isAdmin) {
        this.productId = product.getId();
        this.name = product.getName();
        this.profileImageUrl = product.getProfileImageUrl();
        this.detailImageUrl = product.getDetailImageUrl();
        this.description = product.getDescription();
        this.originalPrice = product.getOriginalPrice();
        this.salePrice = product.getSalePrice();
        this.tags = product.getProductTags().stream().map(TagInfo::of).toList();
        this.genderPrices = product.getProductGenderPrices().stream().map(GenderPrice::of).toList();
        this.schedules = product.getProductSchedules().stream().map(ScheduleInfo::of).toList();
        this.status = product.getStatus();

        if (isAdmin) {
            this.creatorId = product.getCreatorId();
            this.lastModifierId = product.getLastModifierId();
        }
    }

    public static ProductDto of(Product product) {
        return new ProductDto(product, false);
    }

    public static ProductDto of(Product product, boolean isAdmin) {
        return new ProductDto(product, isAdmin);
    }
}
