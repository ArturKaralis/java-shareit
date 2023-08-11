package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.enam.BookingStatus;
import ru.practicum.shareit.item.dto.GetBookingForItemDto;
import ru.practicum.shareit.user.dto.GetBookingUserDto;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class GetBookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private GetBookingUserDto booker;
    private GetBookingForItemDto item;
}
