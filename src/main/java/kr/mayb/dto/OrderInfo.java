package kr.mayb.dto;

import kr.mayb.data.model.Order;
import kr.mayb.enums.PaymentMethod;
import kr.mayb.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record OrderInfo(
        long orderId,

        int totalPrice,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        boolean hasReviewed,

        String productName,
        String productProfileImage,

        String gender,
        LocalDateTime scheduledAt,

        String customerName,

        OffsetDateTime createdAt
) {
    public static OrderInfo of(Order order, OrderedProductItem productItem, String customerName) {
        return new OrderInfo(
                order.getId(),
                order.getTotalPrice(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.isHasReviewed(),
                productItem.product().getName(),
                productItem.product().getProfileImageUrl(),
                productItem.genderPrice().getGender(),
                productItem.schedule().getTimeSlot(),
                customerName,
                order.getCreatedAt()
        );
    }
}
