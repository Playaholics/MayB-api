package kr.mayb.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.mayb.dto.ProductDto;
import kr.mayb.dto.ProductRegistrationRequest;
import kr.mayb.facade.ProductFacade;
import kr.mayb.security.DenyAll;
import kr.mayb.security.PermitAdmin;
import kr.mayb.util.response.ApiResponse;
import kr.mayb.util.response.Responses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "Product", description = "상품 관련 API")
@DenyAll
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacade productFacade;

    @PermitAdmin
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductDto>> registerProduct(@RequestPart(value = "profile") MultipartFile profileImage,
                                                                   @RequestPart(value = "detail") MultipartFile detailImage,
                                                                   @RequestPart(value = "product") @Valid ProductRegistrationRequest request) {
        ProductDto response = productFacade.registerProduct(profileImage, detailImage, request);
        return Responses.ok(response);
    }
}
