package kr.mayb.data.repository;


import kr.mayb.data.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    List<Member> findAllByIdIn(Collection<Long> memberIds);
}
