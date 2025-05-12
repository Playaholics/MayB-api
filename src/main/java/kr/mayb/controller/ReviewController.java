package kr.mayb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.mayb.dto.ReviewDto;
import kr.mayb.facade.ReviewFacade;
import kr.mayb.security.PermitAll;
import kr.mayb.util.request.PageRequest;
import kr.mayb.util.response.ApiResponse;
import kr.mayb.util.response.PageResponse;
import kr.mayb.util.response.Responses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Review", description = "리뷰 API")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewFacade reviewFacade;

    @Operation(summary = "상품 리뷰 조회")
    @PermitAll
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<PageResponse<ReviewDto, Void>>> getReviews(@RequestParam(value = "pid") long productId, PageRequest pageRequest) {
        PageResponse<ReviewDto, Void> response = reviewFacade.getReviews(productId, pageRequest);
        return Responses.ok(response);
    }
}
