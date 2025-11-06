package kr.mayb.data.repository;

import kr.mayb.data.model.UserQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserQuestionRepository extends JpaRepository<UserQuestion, Long>, JpaSpecificationExecutor<UserQuestion> {
}
