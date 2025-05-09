package kr.mayb.dto;

import kr.mayb.data.model.ProductSchedule;

import java.time.LocalDateTime;

public record ScheduleInfo(
        long id,
        LocalDateTime time
) {
    public static ScheduleInfo of(ProductSchedule schedule) {
        return new ScheduleInfo(schedule.getId(), schedule.getTimeSlot());
    }
}
