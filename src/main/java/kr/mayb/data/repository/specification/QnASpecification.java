package kr.mayb.data.repository.specification;

import kr.mayb.data.model.UserQuestion;
import kr.mayb.enums.QnAStatus;
import org.springframework.data.jpa.domain.Specification;

public class QnASpecification {
    public static Specification<UserQuestion> withProductId(Long productId) {
        return (root, query, criteriaBuilder) ->
                productId != null ? criteriaBuilder.equal(root.get("productId"), productId) : criteriaBuilder.conjunction();
    }

    public static Specification<UserQuestion> withOnlyMine(Long memberId, boolean onlyMine) {
        return (root, query, criteriaBuilder) -> {
            if (!onlyMine || memberId == null) {
                return criteriaBuilder.conjunction();
            } else {
                return criteriaBuilder.equal(root.get("memberId"), memberId);
            }
        };
    }

    public static Specification<UserQuestion> withExcludeSecret(boolean excludeSecret) {
        return (root, query, criteriaBuilder) ->
                excludeSecret ? criteriaBuilder.isFalse(root.get("isSecret")) : criteriaBuilder.conjunction();
    }

    public static Specification<UserQuestion> withStatus(QnAStatus status) {
        return (root, query, criteriaBuilder) ->
                switch (status) {
                    case ALL -> criteriaBuilder.conjunction();
                    case ANSWERED -> criteriaBuilder.isNotNull(root.get("answer"));
                    case UNANSWERED -> criteriaBuilder.isNull(root.get("answer"));
                };
    }
}
