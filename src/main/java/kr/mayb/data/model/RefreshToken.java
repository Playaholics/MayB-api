package kr.mayb.data.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class RefreshToken {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column
    private long memberId;

    @Column
    private String refreshToken;

    @Column
    private OffsetDateTime expiresAt;

    @Setter(AccessLevel.NONE)
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void touchForCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}