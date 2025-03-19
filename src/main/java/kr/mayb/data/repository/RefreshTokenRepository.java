package kr.mayb.data.repository;

import kr.mayb.data.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByMemberIdAndToken(long memberId, String token);
}
