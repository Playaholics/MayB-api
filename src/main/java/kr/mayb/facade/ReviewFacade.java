package kr.mayb.facade;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import kr.mayb.data.model.Member;
import kr.mayb.data.model.Product;
import kr.mayb.data.model.Review;
import kr.mayb.data.model.ReviewImage;
import kr.mayb.dto.*;
import kr.mayb.enums.GcsBucketPath;
import kr.mayb.enums.PaymentStatus;
import kr.mayb.error.BadRequestException;
import kr.mayb.error.ExternalApiException;
import kr.mayb.error.ResourceNotFoundException;
import kr.mayb.service.*;
import kr.mayb.util.ContextUtils;
import kr.mayb.util.request.PageRequest;
import kr.mayb.util.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class ReviewFacade {

    private final ReviewService reviewService;
    private final ImageService imageService;
    private final MemberService memberService;
    private final ProductService productService;
    private final OrderService orderService;

    private final ThreadPoolTaskExecutor executor;

    @Transactional
    public ReviewDto writeReview(ReviewRequest request, List<MultipartFile> images) {
        MemberDto member = ContextUtils.getCurrentMember()
                .orElseThrow(() -> new BadRequestException("Only signed-in members can write reviews."));

        Member author = memberService.getMember(member.getMemberId());
        Product product = productService.getProduct(request.productId());
        Pair<Long, OrderedProductItem> orderItem = getParticipatedProduct(author, product);

        if (images.isEmpty()) {
            Review saved = reviewService.save(request, product.getId(), orderItem, List.of(), author);
            orderService.updateReviewStatus(orderItem.getLeft());
            return ReviewDto.of(saved, author.getId());
        }

        List<String> imageUrls = uploadImages(images);
        Review saved = reviewService.save(request, product.getId(), orderItem, imageUrls, author);
        orderService.updateReviewStatus(orderItem.getLeft());
        return ReviewDto.of(saved, author.getId());
    }

    public ReviewDto getReview(long reviewId) {
        Review review = reviewService.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found. : " + reviewId));

        return ReviewDto.of(review);
    }

    public PageResponse<ReviewDto, Void> getReviews(long productId, PageRequest pageRequest) {
        Long currentMemberId = ContextUtils.getCurrentMember()
                .map(MemberDto::getMemberId)
                .orElse(null);

        Page<Review> reviews = reviewService.findAllByProductId(productId, pageRequest);
        return PageResponse.of(new PageImpl<>(convertToReviewDto(reviews, currentMemberId), reviews.getPageable(), reviews.getTotalElements()));
    }

    public ReviewDto updateReview(long reviewId, @NotBlank String content, int starRating) {
        MemberDto member = ContextUtils.loadMember();

        Review updated = reviewService.updateReview(reviewId, content, starRating, member.getMemberId());
        return ReviewDto.of(updated, member.getMemberId());
    }

    public ImageDto addReviewImage(long reviewId, MultipartFile image) {
        MemberDto member = ContextUtils.loadMember();
        Review review = reviewService.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found. : " + reviewId));

        if (review.getMember().getId() != member.getMemberId()) {
            throw new AccessDeniedException("Only author can update review. : " + member.getMemberId());
        }

        String imageUrl = imageService.upload(image, GcsBucketPath.REVIEW);
        ReviewImage saved = reviewService.addImage(review, imageUrl);

        return ImageDto.of(saved);
    }

    private Pair<Long, OrderedProductItem> getParticipatedProduct(Member author, Product product) {
        return orderService.findByProductIdAndMemberId(author.getId(), product.getId())
                .filter(orderOpt -> orderOpt.getPaymentStatus() == PaymentStatus.COMPLETED)
                .map(orderOpt -> {
                    OrderedProductItem productItem = productService.findOrderedProductItem(product.getId(), orderOpt.getId(), orderOpt.getProductScheduleId());
                    return Pair.of(orderOpt.getId(), productItem);
                })
                .orElseThrow(() -> new BadRequestException("Only members who have purchased the product can write reviews."));
    }

    private List<String> uploadImages(List<MultipartFile> images) {
        // Upload images asynchronously
        Map<Integer, CompletableFuture<String>> uploadImageAsyncMap = IntStream.range(0, images.size())
                .boxed()
                .collect(Collectors.toMap(
                        index -> index,
                        index -> CompletableFuture.supplyAsync(() ->
                                        imageService.upload(images.get(index), GcsBucketPath.REVIEW), executor)
                                .exceptionally(e -> {
                                    throw new ExternalApiException("Failed to upload review Images" + e.getMessage());
                                })
                ));

        // Wait for all asynchronous image uploads to complete, sort by original order, and return the list of URLs.
        return uploadImageAsyncMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .map(CompletableFuture::join)
                .toList();
    }

    private List<ReviewDto> convertToReviewDto(Page<Review> reviews, Long currentMemberId) {
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
}
