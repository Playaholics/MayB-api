package kr.mayb.service;

import kr.mayb.data.model.Review;
import kr.mayb.data.repository.ReviewRepository;
import kr.mayb.enums.ReviewSort;
import kr.mayb.util.request.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Page<Review> findAllByProductId(long productId, PageRequest pageRequest) {
        Sort sort = ReviewSort.find(pageRequest.getSort())
                .map(ReviewSort::toSortOption)
                .orElse(ReviewSort.NEWEST_FIRST.toSortOption());
        Pageable pageable = pageRequest.toPageable(sort);

        return reviewRepository.findAllByProductId(productId, pageable);
    }
}
