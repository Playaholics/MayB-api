package kr.mayb.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum OrderSort {
    NEWEST_FIRST("createdAt"),
    ;

    private final String value;

    public Sort toSortOption() {
        return Sort.by(Sort.Direction.DESC, NEWEST_FIRST.getValue());
    }
}
