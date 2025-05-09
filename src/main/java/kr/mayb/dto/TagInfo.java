package kr.mayb.dto;

import kr.mayb.data.model.ProductTag;

public record TagInfo(
        long tagId,
        String name
) {
    public static TagInfo of(ProductTag tag) {
        return new TagInfo(tag.getId(), tag.getName());
    }
}
