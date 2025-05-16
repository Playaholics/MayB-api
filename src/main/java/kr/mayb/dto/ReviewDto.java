package kr.mayb.dto;

import kr.mayb.data.model.Member;
import kr.mayb.data.model.Review;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public record ReviewDto(
        long reviewId,
        String content,
        String author,
        int starRating,
        String gender,
        LocalDateTime scheduledAt,
        long memberId,
        List<ImageDto> images,
        OffsetDateTime createdAt,
        boolean isMyReview
) {
    private ReviewDto(Review review, List<ImageDto> images, Member author, boolean isMyReview) {
        this(
                review.getId(),
                review.getContent(),
                author.getMaskedName(),
                review.getStarRating(),
                review.getGender(),
                review.getScheduledAt(),
                author.getId(),
                images,
                review.getCreatedAt(),
                isMyReview
        );
    }

    public static ReviewDto of(Review review) {
        Member author = review.getMember();
        List<ImageDto> images = review.getReviewImages().stream().map(ImageDto::of).toList();
        return new ReviewDto(review, images, author, false);
    }

    public static ReviewDto of(Review review, long currentMemberId) {
        Member author = review.getMember();
        List<ImageDto> images = review.getReviewImages().stream().map(ImageDto::of).toList();
        boolean isMyReview = isMyReview(author.getId(), currentMemberId);
        return new ReviewDto(review, images, author, isMyReview);
    }

    private static boolean isMyReview(long authorId, long currentMemberId) {
        return authorId == currentMemberId;
    }
}
