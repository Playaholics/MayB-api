package kr.mayb.service;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductGenderPrice;
import kr.mayb.data.model.ProductSchedule;
import kr.mayb.data.model.ProductTag;
import kr.mayb.data.repository.ProductGenderPriceRepository;
import kr.mayb.data.repository.ProductRepository;
import kr.mayb.data.repository.ProductScheduleRepository;
import kr.mayb.data.repository.ProductTagRepository;
import kr.mayb.dto.*;
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
    private final ProductGenderPriceRepository productGenderPriceRepository;
    private final ProductScheduleRepository productScheduleRepository;

    @Transactional
    public ProductDto registerProduct(ProductRegistrationRequest request, String profileUrl, String detailUrl, long creatorId) {
        Product product = new Product();
        product.setName(request.name());
        product.setOriginalPrice(request.originalPrice());
        product.setDiscountPrice(request.salePrice());
        product.setProfileImageUrl(profileUrl);
        product.setDetailImageUrl(detailUrl);
        product.setDescription(request.description());
        product.setCreatorId(creatorId);
        product.setLastModifierId(creatorId);
        product.setStatus(ProductStatus.ACTIVE);

        saveAdditionalInfo(request.tags(), request.schedules(), request.genderPrices(), product);

        Product saved = productRepository.save(product);
        return ProductDto.of(saved, true);
    }

    private void saveAdditionalInfo(List<String> tags, List<LocalDateTime> schedules, List<GenderPrice> genderPrices, Product product) {
        List<ProductTag> productTags = tags.stream()
                .filter(StringUtils::isNotBlank)
                .map(tag -> {
                    ProductTag productTag = new ProductTag();
                    productTag.setName(tag);
                    productTag.setProduct(product);
                    return productTag;
                })
                .collect(Collectors.toList());

        List<ProductSchedule> productSchedules = schedules.stream()
                .filter(Objects::nonNull)
                .map(time -> {
                    ProductSchedule productSchedule = new ProductSchedule();
                    productSchedule.setTimeSlot(time);
                    productSchedule.setProduct(product);
                    return productSchedule;
                })
                .collect(Collectors.toList());

        List<ProductGenderPrice> productGenderPrices = genderPrices.stream()
                .map(genderPrice -> {
                    ProductGenderPrice productGenderPrice = new ProductGenderPrice();
                    productGenderPrice.setGender(genderPrice.gender());
                    productGenderPrice.setPrice(genderPrice.price());
                    productGenderPrice.setProduct(product);
                    return productGenderPrice;
                })
                .collect(Collectors.toList());

        product.setProductTags(productTags);
        product.setProductSchedules(productSchedules);
        product.setProductGenderPrices(productGenderPrices);
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
        Product product = getProduct(productId);

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
        Product product = getProduct(productId);

        product.setName(request.name());
        product.setOriginalPrice(request.originalPrice());
        product.setDiscountPrice(request.salePrice());
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
        Product product = getProduct(productId);

        if (active) {
            product.setStatus(ProductStatus.ACTIVE);
        } else {
            product.setStatus(ProductStatus.INACTIVE);
        }

        product.setLastModifierId(memberId);
    }

    public OrderedProductItem findOrderedProductItem(long productId, long priceId, long scheduleId) {
        Product product = getProduct(productId);
        ProductGenderPrice genderPrice = getGenderPrice(priceId, product);
        ProductSchedule schedule = getSchedule(scheduleId, product);

        return OrderedProductItem.of(product, genderPrice, schedule);
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
        productGenderPriceRepository.deleteByProduct(product);
        productScheduleRepository.deleteByProduct(product);
        saveAdditionalInfo(request.tags(), request.schedules(), request.genderPrices(), product);
    }

    private ProductSchedule getSchedule(long scheduleId, Product product) {
        return productScheduleRepository.findByIdAndProduct(scheduleId, product)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found: " + scheduleId));
    }

    private ProductGenderPrice getGenderPrice(long priceId, Product product) {
        return productGenderPriceRepository.findByIdAndProduct(priceId, product)
                .orElseThrow(() -> new ResourceNotFoundException("Price not found: " + priceId));
    }

    private Product getProduct(long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }
}
