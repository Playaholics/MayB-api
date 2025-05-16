package kr.mayb.service;

import jakarta.transaction.Transactional;
import kr.mayb.data.model.Member;
import kr.mayb.data.model.Review;
import kr.mayb.data.model.ReviewImage;
import kr.mayb.data.repository.ReviewImageRepository;
import kr.mayb.data.repository.ReviewRepository;
import kr.mayb.dto.OrderedProductItem;
import kr.mayb.dto.ReviewRequest;
import kr.mayb.enums.ReviewSort;
import kr.mayb.error.ResourceNotFoundException;
import kr.mayb.util.request.PageRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

    @Transactional
    public Review save(ReviewRequest request, long productId, Pair<Long, OrderedProductItem> orderItem, List<String> imageUrls, Member author) {
        Long orderId = orderItem.getLeft();
        OrderedProductItem orderedProduct = orderItem.getRight();
        String gender = orderedProduct.genderPrice().getGender();
        LocalDateTime scheduledAt = orderedProduct.schedule().getTimeSlot();

        Review review = new Review();
        review.setContent(request.content());
        review.setStarRating(request.starRating());
        review.setGender(gender);
        review.setScheduledAt(scheduledAt);
        review.setProductId(productId);
        review.setOrderId(orderId);
        review.setMember(author);

        saveImages(review, imageUrls);

        return reviewRepository.save(review);
    }

    private void saveImages(Review review, List<String> imageUrls) {
        List<ReviewImage> reviewImages = imageUrls.stream()
                .map(url -> {
                    ReviewImage reviewImage = new ReviewImage();
                    reviewImage.setReview(review);
                    reviewImage.setImageUrl(url);
                    return reviewImage;
                })
                .toList();

        review.setReviewImages(reviewImages);
    }

    public Page<Review> findAllByProductId(long productId, PageRequest pageRequest) {
        Sort sort = ReviewSort.find(pageRequest.getSort())
                .map(ReviewSort::toSortOption)
                .orElse(ReviewSort.NEWEST_FIRST.toSortOption());
        Pageable pageable = pageRequest.toPageable(sort);

        return reviewRepository.findAllByProductId(productId, pageable);
    }

    @Transactional
    public Review updateReview(long reviewId, String content, int starRating, long memberId) {
        Review review = findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found. : " + reviewId));

        if (review.getMember().getId() != memberId) {
            throw new AccessDeniedException("Only author can update review. : " + memberId);
        }

        review.setContent(content);
        review.setStarRating(starRating);

        return reviewRepository.save(review);
    }

    public Optional<Review> findById(long reviewId) {
        return reviewRepository.findById(reviewId);
    }

    @Transactional
    public ReviewImage addImage(Review review, String imageUrl) {
        ReviewImage reviewImage = new ReviewImage();
        reviewImage.setReview(review);
        reviewImage.setImageUrl(imageUrl);

        return reviewImageRepository.save(reviewImage);
    }

    @Transactional
    public void removeImage(long imageId) {
        reviewImageRepository.deleteById(imageId);
    }

    public Optional<ReviewImage> findImageById(long imageId) {
        return reviewImageRepository.findById(imageId);
    }
}
