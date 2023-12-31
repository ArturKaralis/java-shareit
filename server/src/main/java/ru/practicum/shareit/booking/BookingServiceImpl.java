package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.enam.BookingState;
import ru.practicum.shareit.booking.enam.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidDateException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.OffsetBasedPageRequest;
import ru.practicum.shareit.validator.StartBeforeEndDateValid;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constants.SORT_BY_START_DATE_DESC;

@Service
@RequiredArgsConstructor
@StartBeforeEndDateValid
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Transactional(readOnly = true)
    @Override
    public List<GetBookingDto> getUserBookings(long userId, String stateString, int from, int size) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")
        );

        BookingState state = BookingState.valueOf(stateString.toUpperCase());
        LocalDateTime currentMoment = LocalDateTime.now();
        List<Booking> bookings;
        Pageable pageable = new OffsetBasedPageRequest(from, size, SORT_BY_START_DATE_DESC);

        switch (state) {
            case ALL:
                bookings = bookingStorage.findByBooker(user, pageable);
                break;
            case CURRENT:
                bookings = bookingStorage.findByBookerCurrent(user, currentMoment, pageable);
                break;
            case PAST:
                bookings = bookingStorage.findByBookerPast(user, currentMoment, pageable);
                break;
            case FUTURE:
                bookings = bookingStorage.findByBookerFuture(user, currentMoment, pageable);
                break;
            case WAITING:
                bookings = bookingStorage.findByBookerAndStatus(user, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingStorage.findByBookerAndStatus(user, BookingStatus.REJECTED, pageable);
                break;
            default:
                bookings = Collections.emptyList();
        }

        return bookings
                .stream()
                .map(BookingMapper::toGetBookingDtoFromBooking)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetBookingDto> getOwnerBookings(long userId, String stateString, int from, int size) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")
        );

        BookingState state = BookingState.valueOf(stateString.toUpperCase());
        LocalDateTime currentMoment = LocalDateTime.now();
        List<Booking> bookings;
        Pageable pageable = new OffsetBasedPageRequest(from, size, SORT_BY_START_DATE_DESC);

        switch (state) {
            case ALL:
                bookings = bookingStorage.findByItemOwner(user, pageable);
                break;
            case CURRENT:
                bookings = bookingStorage.findByItemOwnerCurrent(user, currentMoment, pageable);
                break;
            case PAST:
                bookings = bookingStorage.findByItemOwnerPast(user, currentMoment, pageable);
                break;
            case FUTURE:
                bookings = bookingStorage.findByItemOwnerFuture(user, currentMoment, pageable);
                break;
            case WAITING:
                bookings = bookingStorage.findByItemOwnerAndStatus(user, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingStorage.findByItemOwnerAndStatus(user, BookingStatus.REJECTED, pageable);
                break;
            default:
                bookings = Collections.emptyList();
        }

        return bookings
                .stream()
                .map(BookingMapper::toGetBookingDtoFromBooking)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public GetBookingDto getBookingByUserOwner(long userId, long bookingId) {
        userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")
        );
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование не найдено")
        );

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Бронирование не найдено");
        }

        return BookingMapper.toGetBookingDtoFromBooking(booking);
    }

    @Override
    public GetBookingDto create(long userId, CreateBookingDto createBookingDto) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")
        );

        if (createBookingDto.getEnd().isBefore(createBookingDto.getStart()) ||
                createBookingDto.getEnd().isEqual(createBookingDto.getStart())) {
            throw new NotValidDateException("Дата окончания не может быть раньше или равна дате начала");
        }

        Item item = itemStorage.findById(createBookingDto.getItemId()).orElseThrow(
                () -> new NotFoundException("Вещь не найдена")
        );

        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь не доступна для бронирования");
        }

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Нельзя забронировать свою вещь");
        }

        Booking booking = BookingMapper.toBookingFromCreateBookingDto(createBookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toGetBookingDtoFromBooking(bookingStorage.save(booking));
    }

    @Override
    public GetBookingDto approveBooking(long userId, long bookingId, Boolean approved) {
        userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")
        );
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование не найдено")
        );

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Бронирование не найдено");
        }

        BookingStatus status;

        if (approved) {
            if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new NotAvailableException("Бронирование уже подтверждено");
            }
            status = BookingStatus.APPROVED;
        } else {
            status = BookingStatus.REJECTED;
        }

        booking.setStatus(status);

        return BookingMapper.toGetBookingDtoFromBooking(booking);
    }
}
