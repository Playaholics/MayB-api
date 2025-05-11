package kr.mayb.data.repository.specification;

import kr.mayb.data.model.Order;
import kr.mayb.enums.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

    public static Specification<Order> withProductId(Long productId) {
        return (root, query, criteriaBuilder) ->
                productId != null ? criteriaBuilder.equal(root.get("productId"), productId) : criteriaBuilder.conjunction();
    }

    public static Specification<Order> withPaymentStatus(PaymentStatus paymentStatus) {
        return (root, query, criteriaBuilder) ->
                paymentStatus != null ? criteriaBuilder.equal(root.get("paymentStatus"), paymentStatus) : criteriaBuilder.conjunction();
    }
}
