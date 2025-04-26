package kr.mayb.data.model;

import java.time.OffsetDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(schema = "mayb")
@Entity
public class RefreshToken {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column
    private long memberId;

    @Column
    private String token;

    @Setter(AccessLevel.NONE)
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void touchForCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}