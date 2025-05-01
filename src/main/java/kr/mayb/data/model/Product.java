package kr.mayb.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(schema = "mayb")
@Entity
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @Column
    private int originalPrice;

    @Column
    private int salePrice;

    @Column
    private String productImageUrl;

    @Column
    private String productDetailImageUrl;

    @Column
    private String description;
}
