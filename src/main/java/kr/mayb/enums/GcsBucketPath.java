package kr.mayb.enums;

import kr.mayb.error.BadRequestException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GcsBucketPath {
    PROFILE("profile/"),
    PRODUCT_PROFILE("product_profile/"),
    PRODUCT_DETAIL("product_detail/"),
    ;

    private final String value;

    public static String getPath(GcsBucketPath pathType) {
        return switch (pathType) {
            case PROFILE -> PROFILE.value;
            case PRODUCT_PROFILE -> PRODUCT_PROFILE.value;
            case PRODUCT_DETAIL -> PRODUCT_DETAIL.value;
            default -> throw new BadRequestException("Invalid GcsPathType" + pathType);
        };
    }
}
