package kr.mayb.data.model;

import jakarta.persistence.*;
import kr.mayb.enums.PaymentMethod;
import kr.mayb.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(schema = "mayb")
@Entity
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private int totalPrice;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private long productId;

    @Column
    private long productGenderPriceId;

    @Column
    private long productScheduleId;

    @Column(nullable = false)
    private long memberId;

    @Column(nullable = false)
    private boolean hasReviewed;
}
