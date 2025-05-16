package kr.mayb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.mayb.dto.ImageDto;
import kr.mayb.dto.ReviewDto;
import kr.mayb.dto.ReviewRequest;
import kr.mayb.error.BadRequestException;
import kr.mayb.facade.ReviewFacade;
import kr.mayb.security.PermitAll;
import kr.mayb.security.PermitAuthenticated;
import kr.mayb.util.request.PageRequest;
import kr.mayb.util.response.ApiResponse;
import kr.mayb.util.response.PageResponse;
import kr.mayb.util.response.Responses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Review", description = "리뷰 API")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewFacade reviewFacade;

    @Operation(summary = "상품 리뷰 작성")
    @PermitAuthenticated
    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<ReviewDto>> writeReview(@RequestPart("review") ReviewRequest request,
                                                              @RequestPart("images") List<MultipartFile> images) {
        if (images.size() > 5) {
            throw new BadRequestException("Review images must be less than 5");
        }

        ReviewDto response = reviewFacade.writeReview(request, images);
        return Responses.ok(response);
    }

    @Operation(summary = "상품 리뷰 상세 조회")
    @PermitAll
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDto>> getReview(@PathVariable long reviewId) {
        ReviewDto response = reviewFacade.getReview(reviewId);
        return Responses.ok(response);
    }

    @Operation(summary = "상품 리뷰 조회")
    @PermitAll
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<PageResponse<ReviewDto, Void>>> getReviews(@RequestParam(value = "pid") long productId,
                                                                                 PageRequest pageRequest) {
        PageResponse<ReviewDto, Void> response = reviewFacade.getReviews(productId, pageRequest);
        return Responses.ok(response);
    }

    @Operation(summary = "상품 리뷰 수정")
    @PermitAuthenticated
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDto>> updateReview(@PathVariable long reviewId, @RequestBody @Valid ReviewUpdateRequest request) {
        ReviewDto response = reviewFacade.updateReview(reviewId, request.content(), request.starRating());
        return Responses.ok(response);
    }

    @Operation(summary = "상품 리뷰 이미지 추가 (리뷰 수정 시)")
    @PermitAuthenticated
    @PostMapping("/reviews/{reviewId}/images")
    public ResponseEntity<ApiResponse<ImageDto>> addReviewImage(@PathVariable long reviewId, @RequestParam("image") MultipartFile image) {
        ImageDto response = reviewFacade.addReviewImage(reviewId, image);
        return Responses.ok(response);
    }

    @Operation(summary = "상품 리뷰 이미지 삭제 (리뷰 수정 시)")
    @PermitAuthenticated
    @DeleteMapping("/reviews/{reviewId}/images/{imageId}")
    public ResponseEntity<Void> removeReviewImage(@PathVariable long reviewId, @PathVariable long imageId) {
        reviewFacade.removeReviewImage(reviewId, imageId);
        return Responses.noContent();
    }

    private record ReviewUpdateRequest(
            @NotBlank
            String content,
            @NotNull
            int starRating
    ) {
    }
}
