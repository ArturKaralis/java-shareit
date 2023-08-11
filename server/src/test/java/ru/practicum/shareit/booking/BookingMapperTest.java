package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {

    @Test
    public void shouldMakeBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now());
        Booking booking = BookingMapper.makeBooking(bookingDto);
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
    }

    @Test
    public void shouldMakeBookingDto() {
        User booker = new User();
        booker.setId(1);
        booker.setName("bookerName");
        Item item = new Item();
        item.setId(2);
        item.setName("itemName");
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());
        booking.setStatus(Status.APPROVED);
        booking.setBooker(booker);
        booking.setItem(item);
        BookingResponseDto bookingDto = BookingMapper.makeBookingDto(booking);
        assertEquals(1, bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals("APPROVED", bookingDto.getStatus().toString());
        assertEquals(1, bookingDto.getBooker().getId());
        assertEquals("bookerName", bookingDto.getBooker().getName());
        assertEquals(2, bookingDto.getItem().getId());
        assertEquals("itemName", bookingDto.getItem().getName());
    }

    @Test
    public void shouldMakeBookingDtoList() {
        User booker = new User();
        booker.setId(1);
        booker.setName("bookerName");
        Item item = new Item();
        item.setId(2);
        item.setName("itemName");
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());
        booking.setStatus(Status.APPROVED);
        booking.setBooker(booker);
        booking.setItem(item);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        List<BookingResponseDto> bookingsDto = BookingMapper.makeListBookingDto(bookings);
        assertEquals(1, bookingsDto.size());
        assertEquals(1, bookingsDto.get(0).getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals("APPROVED", bookingsDto.get(0).getStatus().toString());
        assertEquals(1, bookingsDto.get(0).getBooker().getId());
        assertEquals("bookerName", bookingsDto.get(0).getBooker().getName());
        assertEquals(2, bookingsDto.get(0).getItem().getId());
        assertEquals("itemName", bookingsDto.get(0).getItem().getName());
    }
}