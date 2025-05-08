package kr.mayb.service;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductDateTime;
import kr.mayb.data.model.ProductGender;
import kr.mayb.data.model.ProductTag;
import kr.mayb.data.repository.ProductRepository;
import kr.mayb.dto.GenderPrice;
import kr.mayb.dto.ProductDto;
import kr.mayb.dto.ProductRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductDto registerProduct(ProductRegistrationRequest request, String profileUrl, String detailUrl, long creatorId) {
        Product product = new Product();
        product.setName(request.name());
        product.setOriginalPrice(request.originalPrice());
        product.setSalePrice(request.salePrice());
        product.setProfileImageUrl(profileUrl);
        product.setDetailImageUrl(detailUrl);
        product.setDescription(request.description());
        product.setCreatorId(creatorId);
        product.setLastModifierId(creatorId);

        saveAdditionalInfo(request.tags(), request.dateTimes(), request.genderPrices(), product);

        Product saved = productRepository.save(product);
        return ProductDto.of(saved);
    }

    private void saveAdditionalInfo(List<String> tags, List<LocalDateTime> dateTimes, List<GenderPrice> genderPrices, Product product) {
        List<ProductTag> productTags = tags.stream()
                .filter(StringUtils::isNotBlank)
                .map(tag -> {
                    ProductTag productTag = new ProductTag();
                    productTag.setName(tag);
                    productTag.setProduct(product);
                    return productTag;
                })
                .toList();

        List<ProductDateTime> productDateTimes = dateTimes.stream()
                .filter(Objects::nonNull)
                .map(dateTime -> {
                    ProductDateTime productDateTime = new ProductDateTime();
                    productDateTime.setDateTime(dateTime);
                    productDateTime.setProduct(product);
                    return productDateTime;
                })
                .toList();

        List<ProductGender> productGenders = genderPrices.stream()
                .map(genderPrice -> {
                    ProductGender productGender = new ProductGender();
                    productGender.setGender(genderPrice.gender());
                    productGender.setPrice(genderPrice.price());
                    productGender.setProduct(product);
                    return productGender;
                })
                .toList();

        product.setProductTags(productTags);
        product.setProductDateTimes(productDateTimes);
        product.setProductGenders(productGenders);
    }
}
