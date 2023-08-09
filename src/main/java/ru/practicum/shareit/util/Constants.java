package ru.practicum.shareit.util;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.GetCommentDto;

import java.time.LocalDateTime;
import java.util.Comparator;

public class Constants {
    public static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

    public static final Sort SORT_BY_START_DATE_DESC = Sort.by(Sort.Direction.DESC, "startDate");

    public static final Sort SORT_BY_ID_ASC = Sort.by(Sort.Direction.ASC, "id");

    public static final Comparator<Booking> orderByStartDateDesc = (a, b) -> {
        LocalDateTime aTime = a.getStartDate();
        LocalDateTime bTime = b.getStartDate();
        return bTime.compareTo(aTime);
    };

    public static final Comparator<Booking> orderByStartDateAsc = (a, b) -> {
        LocalDateTime aTime = a.getStartDate();
        LocalDateTime bTime = b.getStartDate();
        return aTime.compareTo(bTime);
    };

    public static final Comparator<GetCommentDto> orderByCreatedDesc = (a, b) -> {
        LocalDateTime aTime = a.getCreated();
        LocalDateTime bTime = b.getCreated();
        return aTime.compareTo(bTime);
    };
}
