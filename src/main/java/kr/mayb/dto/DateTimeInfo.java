package kr.mayb.dto;

import kr.mayb.data.model.ProductDateTime;

import java.time.LocalDateTime;

public record DateTimeInfo(
        long id,
        LocalDateTime dateTime
) {
    public static DateTimeInfo of(ProductDateTime dateTime) {
        return new DateTimeInfo(dateTime.getId(), dateTime.getDateTime());
    }
}
