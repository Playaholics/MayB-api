package kr.mayb.dto;

import kr.mayb.data.model.ReviewImage;

public record ImageDto(
        long reviewImageId,
        String imageUrl
) {
    public static ImageDto of(ReviewImage reviewImage) {
        return new ImageDto(reviewImage.getId(), reviewImage.getImageUrl());
    }
}
