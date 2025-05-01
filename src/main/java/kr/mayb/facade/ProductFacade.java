package kr.mayb.facade;

import kr.mayb.data.model.Product;
import kr.mayb.dto.MemberDto;
import kr.mayb.dto.ProductDto;
import kr.mayb.dto.ProductRegistrationRequest;
import kr.mayb.enums.GcsBucketPath;
import kr.mayb.service.ImageService;
import kr.mayb.service.ProductService;
import kr.mayb.util.ContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ImageService imageService;
    private final ProductService productService;

    public List<ProductDto> registerProduct(MultipartFile profileImage, MultipartFile detailImage, ProductRegistrationRequest request) {
        MemberDto admin = ContextUtils.loadMember();
        String profileUrl = imageService.upload(profileImage, GcsBucketPath.PRODUCT_PROFILE);
        String detailUrl = imageService.upload(detailImage, GcsBucketPath.PRODUCT_DETAIL);

        List<Product> products = productService.registerProduct(request, profileUrl, detailUrl, admin.getMemberId());

        return products
                .stream()
                .filter(Objects::nonNull)
                .map(ProductDto::of)
                .toList();
    }
}
