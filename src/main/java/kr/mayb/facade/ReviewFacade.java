package kr.mayb.facade;

import kr.mayb.data.model.Review;
import kr.mayb.dto.MemberDto;
import kr.mayb.dto.ReviewDto;
import kr.mayb.service.ReviewService;
import kr.mayb.util.ContextUtils;
import kr.mayb.util.request.PageRequest;
import kr.mayb.util.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ReviewFacade {

    private final ReviewService reviewService;

    private static List<ReviewDto> convertToReviewDto(Page<Review> reviews, Long currentMemberId) {
        return reviews.getContent()
                .stream()
                .filter(Objects::nonNull)
                .map(review -> {
                    if (currentMemberId == null) {
                        return ReviewDto.of(review);
                    }

                    // Check whether the review is written by the current signIn member
                    return ReviewDto.of(review, currentMemberId);
                })
                .toList();
    }

    public PageResponse<ReviewDto, Void> getReviews(long productId, PageRequest pageRequest) {
        Long currentMemberId = ContextUtils.getCurrentMember()
                .map(MemberDto::getMemberId)
                .orElse(null);

        Page<Review> reviews = reviewService.findAllByProductId(productId, pageRequest);
        return PageResponse.of(new PageImpl<>(convertToReviewDto(reviews, currentMemberId), reviews.getPageable(), reviews.getTotalElements()));
    }
}
