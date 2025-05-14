package kr.mayb.service;

import jakarta.transaction.Transactional;
import kr.mayb.data.model.Order;
import kr.mayb.data.repository.OrderRepository;
import kr.mayb.data.repository.specification.OrderSpecification;
import kr.mayb.dto.OrderedProductItem;
import kr.mayb.enums.OrderSort;
import kr.mayb.enums.PaymentMethod;
import kr.mayb.enums.PaymentStatus;
import kr.mayb.error.ResourceNotFoundException;
import kr.mayb.util.request.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order makeOrder(OrderedProductItem productItem, PaymentMethod paymentMethod, long memberId) {
        Order order = new Order();
        order.setPaymentMethod(paymentMethod);
        order.setTotalPrice(productItem.genderPrice().getPrice());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setProductId(productItem.product().getId());
        order.setProductGenderPriceId(productItem.genderPrice().getId());
        order.setProductScheduleId(productItem.schedule().getId());
        order.setMemberId(memberId);
        order.setHasReviewed(false);

        return orderRepository.save(order);
    }

    public Page<Order> getMyOrders(long memberId, PageRequest pageRequest) {
        Pageable pageable = pageRequest.toPageable(OrderSort.NEWEST_FIRST.toSortOption());
        return orderRepository.findAllByMemberId(memberId, pageable);
    }

    public Page<Order> getOrders(Long productId, PaymentStatus paymentStatus, PageRequest pageRequest) {
        Pageable pageable = pageRequest.toPageable(OrderSort.NEWEST_FIRST.toSortOption());
        Specification<Order> spec = Specification
                .where(OrderSpecification.withProductId(productId))
                .and(OrderSpecification.withPaymentStatus(paymentStatus));

        return orderRepository.findAll(spec, pageable);
    }

    @Transactional
    public Order updatePaymentStatus(long orderId, long memberId, PaymentStatus paymentStatus) {
        Order order = orderRepository.findByIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("There is no Order with orderId and memberId." + orderId + ", " + memberId));

        order.setPaymentStatus(paymentStatus);

        return orderRepository.save(order);
    }

    @Transactional
    public void updateReviewStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("There is no Order with orderId." + orderId));

        order.setHasReviewed(true);
    }

    public Optional<Order> findByProductIdAndMemberId(long productId, long memberId) {
        return orderRepository.findByProductIdAndMemberId(productId, memberId);
    }
}
