package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.enam.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidDateException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.GetBookingForItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.GetBookingUserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {
    private static BookingService bookingService;
    private static BookingStorage bookingStorage;
    private static ItemStorage itemStorage;
    private static UserStorage userStorage;

    private static User user;
    private static User user2;
    private static Item item;
    private static CreateBookingDto bookingDto;
    private static LocalDateTime startTime;
    private static LocalDateTime endTime;
    private static GetBookingUserDto booker;
    private static Booking booking;
    private static GetBookingForItemDto itemDto;
    private static List<Booking> listOfBookings;

    @BeforeAll
    static void beforeAll() {
        startTime = LocalDateTime.now().minusDays(2);

        endTime = LocalDateTime.now().minusDays(1);

        user = User.builder()
                .id(1L)
                .name("userName")
                .email("mail@ya.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("userName2")
                .email("mail2@ya.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();

        bookingDto = CreateBookingDto.builder()
                .itemId(1L)
                .start(startTime)
                .end(endTime)
                .build();

        booker = GetBookingUserDto.builder()
                .id(2L)
                .build();

        booking = Booking.builder()
                .id(1L)
                .startDate(startTime)
                .endDate(endTime)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();

        itemDto = GetBookingForItemDto.builder()
                .id(1L)
                .name("itemName")
                .build();

        listOfBookings = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            listOfBookings.add(booking.toBuilder().id(i + 1L).build());
        }
    }

    @BeforeEach
    void setUp() {
        bookingStorage = Mockito.mock(BookingStorage.class);
        itemStorage = Mockito.mock(ItemStorage.class);
        userStorage = Mockito.mock(UserStorage.class);
        bookingService = new BookingServiceImpl(bookingStorage, userStorage, itemStorage);
    }

    @Test
    void shouldCreateBooking() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        GetBookingDto getBookingDto = bookingService.create(2L, bookingDto);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime)
                .hasFieldOrPropertyWithValue("status", BookingStatus.WAITING)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("item", itemDto);
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).save(any(Booking.class));
    }

    @Test
    void shouldGetExceptionCreateBookingNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.create(2L, bookingDto)
        );

        assertEquals("Пользователь не найден",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, never()).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void shouldGetExceptionCreateBookingNotFoundItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.create(2L, bookingDto)
        );

        assertEquals("Вещь не найдена",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void shouldGetExceptionCreateBookingNotValidDateException() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotValidDateException exception = Assertions.assertThrows(
                NotValidDateException.class,
                () -> bookingService.create(2L, bookingDto.toBuilder().start(endTime).end(startTime).build())
        );

        assertEquals("Дата окончания не может быть раньше или равна дате начала",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, never()).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void shouldGetExceptionCreateBookingNotAvailableException() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item.toBuilder().available(false).build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotAvailableException exception = Assertions.assertThrows(
                NotAvailableException.class,
                () -> bookingService.create(2L, bookingDto)
        );

        assertEquals("Вещь не доступна для бронирования",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void shouldGetExceptionCreateBookingNotFoundSelfItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.create(1L, bookingDto)
        );

        assertEquals("Нельзя забронировать свою вещь",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void shouldGetExceptionWithApproveBookingNoFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.approveBooking(1L, 1L, true)
        );

        assertEquals("Пользователь не найден",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
        verify(bookingStorage, never()).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithApproveBookingNoFoundBooking() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.approveBooking(1L, 1L, true)
        );

        assertEquals("Бронирование не найдено",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithApproveBookingNoFoundOwner() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.approveBooking(2L, 1L, true)
        );

        assertEquals("Бронирование не найдено",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithApproveBookingNotAvailableAlreadyApproved() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().status(BookingStatus.APPROVED).build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotAvailableException exception = Assertions.assertThrows(
                NotAvailableException.class,
                () -> bookingService.approveBooking(1L, 1L, true)
        );

        assertEquals("Бронирование уже подтверждено",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetBookingByUserOwnerItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));

        GetBookingDto getBookingDto = bookingService.getBookingByUserOwner(1L, 1L);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime)
                .hasFieldOrPropertyWithValue("status", BookingStatus.WAITING)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("item", itemDto);
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetBookingByUserOwnerBooking() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));

        GetBookingDto getBookingDto = bookingService.getBookingByUserOwner(2L, 1L);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime)
                .hasFieldOrPropertyWithValue("status", BookingStatus.WAITING)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("item", itemDto);
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithGetBookingByUserOwnerNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingByUserOwner(1L, 1L)
        );

        assertEquals("Пользователь не найден",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithGetBookingByUserOwnerNotFoundBooking() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingByUserOwner(1L, 1L)
        );

        assertEquals("Бронирование не найдено",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithGetBookingByUserOwnerNotFoundOwner() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingByUserOwner(3L, 1L)
        );

        assertEquals("Бронирование не найдено",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetUserBookingsWithAll() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findByBooker(any(User.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getUserBookings(1L, "aLl", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findByBooker(any(User.class), any(Pageable.class));
    }

    @Test
    void shouldGetExceptionWithGetUserBookingsWithAll() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.findByBooker(any(User.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getUserBookings(1L, "aLl", 1, 5)
        );

        assertEquals("Пользователь не найден",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).findByBooker(any(User.class), any(Pageable.class));
    }

    @Test
    void shouldGetUserBookingsWithCurrent() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findByBookerCurrent(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getUserBookings(1L, "cuRRenT", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findByBookerCurrent(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void shouldGetUserBookingsWithPast() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findByBookerPast(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getUserBookings(1L, "pAST", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findByBookerPast(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void shouldGetUserBookingsWithFuture() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findByBookerFuture(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getUserBookings(1L, "FUTURE", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findByBookerFuture(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void shouldGetUserBookingsWithWaiting() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findByBookerAndStatus(any(User.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getUserBookings(1L, "WAITING", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findByBookerAndStatus(any(User.class), any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void shouldGetExceptionWithGetOwnerBookingsWithAll() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.findByBooker(any(User.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getOwnerBookings(1L, "aLl", 1, 5)
        );

        assertEquals("Пользователь не найден",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).findByBooker(any(User.class), any(Pageable.class));
    }

    @Test
    void shouldGetOwnerBookingsWithWaiting() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findByItemOwnerAndStatus(any(User.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getOwnerBookings(1L, "WAITING", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findByItemOwnerAndStatus(any(User.class), any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void shouldGetOwnerBookingsWithReject() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findByItemOwnerAndStatus(any(User.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getOwnerBookings(1L, "rejected", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findByItemOwnerAndStatus(any(User.class), any(BookingStatus.class), any(Pageable.class));
    }
}