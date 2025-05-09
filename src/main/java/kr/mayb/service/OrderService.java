package kr.mayb.service;

import jakarta.transaction.Transactional;
import kr.mayb.data.model.Order;
import kr.mayb.data.repository.OrderRepository;
import kr.mayb.dto.OrderedProductItem;
import kr.mayb.enums.PaymentMethod;
import kr.mayb.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
