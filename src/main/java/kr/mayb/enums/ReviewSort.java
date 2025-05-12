package kr.mayb.enums;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public enum ReviewSort {
    NEWEST_FIRST("createdAt"),
    RATING_DESC("starRating"),
    RATING_ASC("starRating"),
    ;

    private final String value;

    public static Optional<ReviewSort> find(String name) {
        return Arrays.stream(ReviewSort.values())
                .filter(v -> StringUtils.equalsIgnoreCase(v.name(), name))
                .findFirst();
    }

    public Sort toSortOption() {
        return switch (this) {
            case NEWEST_FIRST -> Sort.by(Sort.Direction.DESC, NEWEST_FIRST.value);
            case RATING_DESC -> Sort.by(Sort.Direction.DESC, RATING_DESC.value);
            case RATING_ASC -> Sort.by(Sort.Direction.ASC, RATING_ASC.value);
        };
    }
}
