package kr.mayb.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.mayb.enums.Provider;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AuthProvider extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id; // 클래스 이름 변경에 따라 필드 이름도 수정

    @Column
    private long memberId;

    @Column(nullable = false)
    private Provider provider; // 소셜 로그인 제공자 (예: Naver, Kakao)

    @Column(nullable = false, unique = true)
    private String providerId; 
}
