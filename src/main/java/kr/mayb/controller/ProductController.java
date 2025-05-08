package kr.mayb.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.mayb.dto.ProductDto;
import kr.mayb.dto.ProductRegistrationRequest;
import kr.mayb.dto.ProductUpdateRequest;
import kr.mayb.facade.ProductFacade;
import kr.mayb.security.DenyAll;
import kr.mayb.security.PermitAdmin;
import kr.mayb.security.PermitAll;
import kr.mayb.util.response.ApiResponse;
import kr.mayb.util.response.ListResponse;
import kr.mayb.util.response.Responses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Tag(name = "Product", description = "상품 관련 API")
@DenyAll
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacade productFacade;

    @Operation(summary = "제품 등록(관리자)")
    @PermitAdmin
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductDto>> registerProduct(@RequestPart(value = "profile") MultipartFile profileImage,
                                                                   @RequestPart(value = "detail") MultipartFile detailImage,
                                                                   @RequestPart(value = "product") @Valid ProductRegistrationRequest request) {
        ProductDto response = productFacade.registerProduct(profileImage, detailImage, request);
        return Responses.ok(response);
    }

    @Operation(summary = "상품 전체 조회", description = "관지자는 모든상품, 일반 사용자는 활성화 상품만 조회 가능")
    @PermitAll
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<ListResponse<ProductDto, Void>>> getProducts() {
        List<ProductDto> response = productFacade.getProducts();
        return Responses.ok(response);
    }

    @Operation(summary = "상품 수정")
    @PermitAdmin
    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(@PathVariable long productId,
                                                                 @RequestPart(value = "profile", required = false) MultipartFile profileImage,
                                                                 @RequestPart(value = "detail", required = false) MultipartFile detailImage,
                                                                 @RequestPart(value = "product") @Valid ProductUpdateRequest request) {
        ProductDto response = productFacade.updateProduct(productId, profileImage, detailImage, request);
        return Responses.ok(response);
    }

    @Operation(summary = "상품 삭제")
    @PermitAdmin
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long productId) {
        productFacade.deleteProduct(productId);
        return Responses.noContent();
    }

    @Operation(summary = "상품 목록에서 숨기기 확성화, 비활성화")
    @PermitAdmin
    @PutMapping("/products/{productId}/{status}")
    public ResponseEntity<Void> changeStatus(@PathVariable long productId, @PathVariable boolean status) {
        productFacade.changeStatus(productId, status);
        return Responses.ok();
    }
}
