package kr.mayb.data.repository;

import kr.mayb.data.model.Authority;
import kr.mayb.enums.AuthorityName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByName(AuthorityName authorityName);
}

