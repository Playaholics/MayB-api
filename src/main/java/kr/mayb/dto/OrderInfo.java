package kr.mayb.dto;

import kr.mayb.data.model.Order;
import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductGenderPrice;
import kr.mayb.data.model.ProductSchedule;
import kr.mayb.enums.PaymentMethod;
import kr.mayb.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

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
        String productName = Optional.ofNullable(productItem.product())
                .map(Product::getName)
                .orElse(null);
        String productProfileImageUrl = Optional.ofNullable(productItem.product())
                .map(Product::getProfileImageUrl)
                .orElse(null);
        String gender = Optional.of(productItem.genderPrice())
                .map(ProductGenderPrice::getGender)
                .orElse(null);
        LocalDateTime scheduledAt = Optional.ofNullable(productItem.schedule())
                .map(ProductSchedule::getTimeSlot)
                .orElse(null);

        return new OrderInfo(
                order.getId(),
                order.getTotalPrice(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.isHasReviewed(),
                productName,
                productProfileImageUrl,
                gender,
                scheduledAt,
                customerName,
                order.getCreatedAt()
        );
    }
}
