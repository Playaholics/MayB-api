package kr.mayb.service;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductDateTime;
import kr.mayb.data.model.ProductGender;
import kr.mayb.data.model.ProductTag;
import kr.mayb.data.repository.ProductDateTimeRepository;
import kr.mayb.data.repository.ProductGenderRepository;
import kr.mayb.data.repository.ProductRepository;
import kr.mayb.data.repository.ProductTagRepository;
import kr.mayb.dto.GenderPrice;
import kr.mayb.dto.ProductDto;
import kr.mayb.dto.ProductRegistrationRequest;
import kr.mayb.dto.ProductUpdateRequest;
import kr.mayb.enums.GcsBucketPath;
import kr.mayb.enums.ProductStatus;
import kr.mayb.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ImageService imageService;

    private final ProductRepository productRepository;
    private final ProductTagRepository productTagRepository;
    private final ProductGenderRepository productGenderRepository;
    private final ProductDateTimeRepository productDateTimeRepository;

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
        product.setStatus(ProductStatus.ACTIVE);

        saveAdditionalInfo(request.tags(), request.dateTimes(), request.genderPrices(), product);

        Product saved = productRepository.save(product);
        return ProductDto.of(saved, true);
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
                .collect(Collectors.toList());

        List<ProductDateTime> productDateTimes = dateTimes.stream()
                .filter(Objects::nonNull)
                .map(dateTime -> {
                    ProductDateTime productDateTime = new ProductDateTime();
                    productDateTime.setDateTime(dateTime);
                    productDateTime.setProduct(product);
                    return productDateTime;
                })
                .collect(Collectors.toList());

        List<ProductGender> productGenders = genderPrices.stream()
                .map(genderPrice -> {
                    ProductGender productGender = new ProductGender();
                    productGender.setGender(genderPrice.gender());
                    productGender.setPrice(genderPrice.price());
                    productGender.setProduct(product);
                    return productGender;
                })
                .collect(Collectors.toList());

        product.setProductTags(productTags);
        product.setProductDateTimes(productDateTimes);
        product.setProductGenders(productGenders);
    }

    public List<ProductDto> getProducts(boolean isAdmin) {
        List<Product> products = productRepository.findAll();
        Stream<Product> stream = products
                .stream()
                .filter(Objects::nonNull);

        if (isAdmin) {
            return stream
                    .map(product -> ProductDto.of(product, true))
                    .toList();
        }

        return stream
                .filter(product -> product.getStatus() == ProductStatus.ACTIVE)
                .map(ProductDto::of)
                .toList();
    }

    public ProductDto getProduct(long productId, boolean isAdmin) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found.: " + productId));

        if (isAdmin) {
            return ProductDto.of(product, true);
        }

        if (product.getStatus() == ProductStatus.INACTIVE) {
            throw new AccessDeniedException("Product is inactive.: " + productId);
        }

        return ProductDto.of(product);
    }

    @Transactional
    public ProductDto updateProduct(long productId, Optional<String> profileUrl, Optional<String> detailUrl, ProductUpdateRequest request, long modifierId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found.: " + productId));

        product.setName(request.name());
        product.setOriginalPrice(request.originalPrice());
        product.setSalePrice(request.salePrice());
        product.setDescription(request.description());

        updateProductImage(profileUrl, detailUrl, product);
        clearAndUpdateAdditionalInfo(request, product);

        product.setLastModifierId(modifierId);
        Product updated = productRepository.save(product);
        return ProductDto.of(updated, true);
    }

    @Transactional
    public void delete(long productId) {
        productRepository.deleteById(productId);
    }

    @Transactional
    public void changeStatus(long productId, boolean active, long memberId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found.: " + productId));

        if (active) {
            product.setStatus(ProductStatus.ACTIVE);
        } else {
            product.setStatus(ProductStatus.INACTIVE);
        }

        product.setLastModifierId(memberId);
    }

    private void updateProductImage(Optional<String> profileUrl, Optional<String> detailUrl, Product product) {
        profileUrl.ifPresent(url -> {
            imageService.delete(product.getProfileImageUrl(), GcsBucketPath.PRODUCT_PROFILE);
            product.setProfileImageUrl(url);
        });
        detailUrl.ifPresent(url -> {
            imageService.delete(product.getDetailImageUrl(), GcsBucketPath.PRODUCT_DETAIL);
            product.setDetailImageUrl(url);
        });
    }

    private void clearAndUpdateAdditionalInfo(ProductUpdateRequest request, Product product) {
        productTagRepository.deleteByProduct(product);
        productGenderRepository.deleteByProduct(product);
        productDateTimeRepository.deleteByProduct(product);
        saveAdditionalInfo(request.tags(), request.dateTimes(), request.genderPrices(), product);
    }
}
