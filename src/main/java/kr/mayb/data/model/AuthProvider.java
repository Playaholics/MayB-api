package kr.mayb.data.model;

import jakarta.persistence.*;
import kr.mayb.enums.Provider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(schema = "mayb")
@Entity
public class AuthProvider extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column
    private long memberId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(nullable = false, unique = true)
    private String providerId; 
}
