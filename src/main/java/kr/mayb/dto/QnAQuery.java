package kr.mayb.dto;

import kr.mayb.data.model.UserQuestion;
import kr.mayb.data.repository.specification.QnASpecification;
import kr.mayb.enums.QnAStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.jpa.domain.Specification;

@ParameterObject
@Getter
@Setter
@RequiredArgsConstructor
public class QnAQuery {
    private final long productId;
    private final boolean excludeSecret;
    private final QnAStatus status;
    private final boolean onlyMine;
    private Long memberId;

    public static QnAQuery of(long productId, boolean excludeSecret, QnAStatus status, boolean onlyMine) {
        return new QnAQuery(productId, excludeSecret, status, onlyMine);
    }

    public Specification<UserQuestion> toSpecQuery() {
        return Specification
                .where(QnASpecification.withProductId(this.productId)
                        .and(QnASpecification.withExcludeSecret(excludeSecret))
                        .and(QnASpecification.withStatus(this.status))
                        .and(QnASpecification.withOnlyMine(this.memberId, this.onlyMine)));
    }
}
