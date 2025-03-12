package kr.mayb.util.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.mayb.error.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@ParameterObject
@Getter
@RequiredArgsConstructor
public class PageRequest {
    private final int page;
    private final int size;
    private final String sort;

    public static PageRequest getDefault() {
        return new PageRequest(0, 10, null);
    }

    public static Pageable defaultPageable() {
        return defaultPageable(10);
    }

    public static Pageable defaultPageable(int size) {
        return org.springframework.data.domain.PageRequest.of(0, size);
    }

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    public int getOffset() {
        int offset = page * size;
        if (offset + getSize() > 10000) {
            throw new BadRequestException("Page request can't exceed total 10000: " + offset + getSize());
        }
        return offset;
    }

    public Pageable toPageable() {
        return org.springframework.data.domain.PageRequest.of(this.page, this.size);
    }

    public Pageable toPageable(Sort sort) {
        return org.springframework.data.domain.PageRequest.of(this.page, this.size, sort);
    }

    public <T> Page<T> toPage(List<T> content, long total) {
        return new PageImpl<>(content, toPageable(), total);
    }
}
