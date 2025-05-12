package kr.mayb.dto;

import kr.mayb.data.model.Member;
import kr.mayb.data.model.Review;

import java.time.OffsetDateTime;

public record ReviewDto(
        long reviewId,
        String productName,
        String content,
        String author,
        int starRating,
        long memberId,
        OffsetDateTime createdAt,
        boolean isMyReview
) {
    private ReviewDto(Review review, Member author, boolean isMyReview) {
        this(
                review.getId(),
                review.getProductName(),
                review.getContent(),
                author.getMaskedName(),
                review.getStarRating(),
                author.getId(),
                review.getCreatedAt(),
                isMyReview
        );
    }

    public static ReviewDto of(Review review) {
        Member author = review.getMember();
        return new ReviewDto(review, author, false);
    }

    public static ReviewDto of(Review review, long currentMemberId) {
        Member author = review.getMember();
        boolean isMyReview = isMyReview(author.getId(), currentMemberId);
        return new ReviewDto(review, author, isMyReview);
    }

    private static boolean isMyReview(long authorId, long currentMemberId) {
        return authorId == currentMemberId;
    }
}
