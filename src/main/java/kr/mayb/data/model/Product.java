package kr.mayb.data.model;

import jakarta.persistence.*;
import kr.mayb.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(schema = "mayb")
@Entity
public class Product extends BaseEntity {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @Column
    private int originalPrice;

    @Column
    private int salePrice;

    @Column
    private String profileImageUrl;

    @Column
    private String detailImageUrl;

    @Column
    private String description;

    @Column
    private long creatorId;

    @Column
    private long lastModifierId;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @BatchSize(size = 20)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductTag> productTags = new ArrayList<>();

    @BatchSize(size = 20)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductGenderPrice> productGenderPrices = new ArrayList<>();

    @BatchSize(size = 20)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductSchedule> productSchedules = new ArrayList<>();
}
