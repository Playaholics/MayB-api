package kr.mayb.service;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductDateTime;
import kr.mayb.data.model.ProductGender;
import kr.mayb.data.model.ProductTag;
import kr.mayb.data.repository.ProductRepository;
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
    public List<Product> registerProduct(ProductRegistrationRequest request, String profileUrl, String detailUrl, long creatorId) {
        createProduct(request, profileUrl, detailUrl, creatorId);
        return productRepository.findAll();
    }

    private void createProduct(ProductRegistrationRequest request, String profileUrl, String detailUrl, long creatorId) {
        Product product = new Product();
        product.setName(request.name());
        product.setOriginalPrice(request.originalPrice());
        product.setSalePrice(request.salePrice());
        product.setProfileImageUrl(profileUrl);
        product.setDetailImageUrl(detailUrl);
        product.setDescription(request.description());
        product.setCreatorId(creatorId);
        product.setLastModifierId(creatorId);

        saveAdditionalInfo(request.tags(), request.dateTimes(), request.genders(), product);

        productRepository.save(product);
    }

    private void saveAdditionalInfo(List<String> tags, List<LocalDateTime> dateTimes, List<String> genders, Product product) {
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

        List<ProductGender> productGenders = genders.stream()
                .filter(StringUtils::isNotBlank)
                .map(gender -> {
                    ProductGender productGender = new ProductGender();
                    productGender.setGender(gender);
                    productGender.setProduct(product);
                    return productGender;
                })
                .toList();

        product.setProductTags(productTags);
        product.setProductDateTimes(productDateTimes);
        product.setProductGenders(productGenders);
    }
}
