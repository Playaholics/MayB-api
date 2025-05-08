package kr.mayb.facade;

import kr.mayb.dto.MemberDto;
import kr.mayb.dto.ProductDto;
import kr.mayb.dto.ProductRegistrationRequest;
import kr.mayb.enums.AuthorityName;
import kr.mayb.enums.GcsBucketPath;
import kr.mayb.service.ImageService;
import kr.mayb.service.ProductService;
import kr.mayb.util.ContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ImageService imageService;
    private final ProductService productService;

    public ProductDto registerProduct(MultipartFile profileImage, MultipartFile detailImage, ProductRegistrationRequest request) {
        MemberDto admin = ContextUtils.loadMember();
        String profileUrl = imageService.upload(profileImage, GcsBucketPath.PRODUCT_PROFILE);
        String detailUrl = imageService.upload(detailImage, GcsBucketPath.PRODUCT_DETAIL);

        return productService.registerProduct(request, profileUrl, detailUrl, admin.getMemberId());
    }

    public List<ProductDto> getProducts() {
        boolean isAdmin = ContextUtils.getCurrentMember()
                .map(MemberDto::getAuthorities)
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(name -> name == AuthorityName.ROLE_ADMIN);

        return productService.getProducts(isAdmin);
    }
}
