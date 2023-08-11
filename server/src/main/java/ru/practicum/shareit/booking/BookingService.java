package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

public interface BookingService {

    List<BookingResponseDto> findByBookerId(Integer userId, Integer from, Integer size);

    BookingResponseDto save(BookingDto bookingDto, Integer bookerId);

    BookingResponseDto update(Integer userId, Boolean isApproved, Integer bookingId);

    BookingResponseDto getById(Integer userId, Integer bookingId);

    List<BookingResponseDto> findByOwnerId(Integer ownerId, Integer from, Integer size);

    List<BookingResponseDto> findByStateUser(Integer userId, State state);

    List<BookingResponseDto> findByStateOwner(Integer userId, State state);

}
