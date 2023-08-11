package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking makeBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }

    public static BookingResponseDto makeBookingDto(Booking booking) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto(booking.getBooker().getId(),
                booking.getBooker().getName(), booking.getItem().getId(), booking.getItem().getName());
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setStatus(booking.getStatus());
        return bookingResponseDto;
    }

    public static List<BookingResponseDto> makeListBookingDto(List<Booking> booking) {
        return booking.stream().map(BookingMapper::makeBookingDto).collect(Collectors.toList());
    }
}
