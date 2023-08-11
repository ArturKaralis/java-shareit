package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<BookingResponseDto> findByBookerId(Integer userId, Integer from, Integer size) {
        getUserOrException(userId);
        return BookingMapper.makeListBookingDto(bookingRepository.findByBookerId(
                userId, PageRequest.of(from / size, size, Sort.by(DESC, "end"))).getContent());
    }

    @Override
    public List<BookingResponseDto> findByOwnerId(Integer ownerId, Integer from, Integer size) {
        getUserOrException(ownerId);
        return BookingMapper.makeListBookingDto(bookingRepository.findByOwnerId(
                ownerId, PageRequest.of(from / size, size, Sort.by(DESC, "end"))).getContent());
    }

    @Override
    public BookingResponseDto getById(Integer userId, Integer bookingId) {
        Booking booking = getBookingOrException(bookingId);
        getUserOrException(userId);
        if (!Objects.equals(userId, booking.getItem().getOwner().getId())
                && !Objects.equals(userId, booking.getBooker().getId())) {
            throw new NotFoundException("Item user");
        }
        return BookingMapper.makeBookingDto(booking);
    }

    @Override
    public List<BookingResponseDto> findByStateUser(Integer userId, State state) {
        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findByStatusAndBookerId(Status.WAITING, userId,
                        Sort.by(DESC, "end"));
                break;
            case REJECTED:
                bookings = bookingRepository.findByStatusAndBookerId(Status.REJECTED, userId,
                        Sort.by(DESC, "end"));
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingUser(LocalDateTime.now(), userId);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingUser(LocalDateTime.now(), userId,
                        Sort.by(DESC, "end"));
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingUser(LocalDateTime.now(), userId,
                        Sort.by(DESC, "end"));
                break;
            default:
                throw new BookingStatusException("Unknown state: " + state);
        }
        return BookingMapper.makeListBookingDto(bookings);
    }

    @Override
    public List<BookingResponseDto> findByStateOwner(Integer ownerId, State state) {
        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findByStatusAndOwnerId(Status.WAITING, ownerId,
                        Sort.by(DESC, "end"));
                break;
            case REJECTED:
                bookings = bookingRepository.findByStatusAndOwnerId(Status.REJECTED, ownerId,
                        Sort.by(DESC, "end"));
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingOwner(LocalDateTime.now(), ownerId);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingOwner(LocalDateTime.now(), ownerId,
                        Sort.by(DESC, "end"));
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingOwner(LocalDateTime.now(), ownerId,
                        Sort.by(DESC, "end"));
                break;
            default:
                throw new BookingStatusException("Unknown state: " + state);
        }
        return BookingMapper.makeListBookingDto(bookings);
    }

    @Override
    @Transactional
    public BookingResponseDto save(BookingDto bookingDto, Integer bookerId) {
        Item item = getItemOrException(bookingDto.getItemId());
        User booker = getUserOrException(bookerId);
        User owner = getUserOrException(item.getOwner().getId());
        if (Objects.equals(owner.getId(), bookerId)) {
            throw new BookingException("Owner can't booking item");
        }
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Item not available now");
        }
        Booking booking = BookingMapper.makeBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return BookingMapper.makeBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto update(Integer userId, Boolean isApproved, Integer bookingId) {
        Booking booking = getBookingOrException(bookingId);
        Item item = getItemOrException(booking.getItem().getId());
        if (booking.getStatus().equals(Status.APPROVED) || booking.getStatus().equals(Status.REJECTED)) {
            throw new BookingStatusException("Booking status can not be changed");
        }
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Item user");
        }
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.makeBookingDto(booking);
    }

    private Booking getBookingOrException(Integer bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId));
    }

    private User getUserOrException(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId));
    }

    private Item getItemOrException(Integer itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId));
    }

}