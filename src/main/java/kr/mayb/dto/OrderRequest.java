package kr.mayb.dto;

import jakarta.validation.constraints.NotNull;
import kr.mayb.enums.PaymentMethod;

public record OrderRequest(
        @NotNull
        long productId,

        @NotNull
        long priceId,

        @NotNull
        long scheduleId,

        @NotNull
        PaymentMethod paymentMethod
) {
}
