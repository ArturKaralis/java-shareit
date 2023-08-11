package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {

    private final EntityManager em;
    private final BookingService service;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    public void save() {
        User owner = addUser("user1", "email1@mail.ru");
        User booker = addUser("user2", "email2@mail.ru");
        Item item = addItem("item1", "description1", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        service.save(bookingDto, booker.getId());
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b " +
                "where b.booker.id = :bookerId", Booking.class);
        Booking booking = query.setParameter("bookerId", booker.getId()).getSingleResult();
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getItem(), equalTo(item));
        assertThat(booking.getBooker(), equalTo(booker));
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    public void update() {
        User owner = addUser("user3", "email3@mail.ru");
        User booker = addUser("user4", "email4@mail.ru");
        Item item = addItem("item2", "description2", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        Boolean isApproved = true;
        service.save(bookingDto, booker.getId());
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b " +
                "where b.booker.id = :bookerId", Booking.class);
        Booking booking = query.setParameter("bookerId", booker.getId()).getSingleResult();
        service.update(owner.getId(), isApproved, booking.getId());
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getItem(), equalTo(item));
        assertThat(booking.getBooker(), equalTo(booker));
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(booking.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    public void findByBookerId() {
        User owner = addUser("user5", "email5@mail.ru");
        User booker = addUser("user6", "email6@mail.ru");
        Item item = addItem("item3", "description3", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        Integer from = 0;
        Integer size = 10;
        service.save(bookingDto, booker.getId());
        List<BookingResponseDto> bookings = service.findByBookerId(booker.getId(), from, size);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(bookings.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookings.get(0).getBooker().getName(), equalTo(booker.getName()));
        assertThat(bookings.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.WAITING));
    }

    @Test
    public void findByOwnerId() {
        User owner = addUser("user7", "email7@mail.ru");
        User booker = addUser("user8", "email8@mail.ru");
        Item item = addItem("item4", "description4", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        Integer from = 0;
        Integer size = 10;
        service.save(bookingDto, booker.getId());
        List<BookingResponseDto> bookings = service.findByOwnerId(owner.getId(), from, size);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(bookings.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookings.get(0).getBooker().getName(), equalTo(booker.getName()));
        assertThat(bookings.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.WAITING));
    }

    @Test
    public void findByStateUserWAITING() {
        User owner = addUser("user9", "email9@mail.ru");
        User booker = addUser("user10", "email10@mail.ru");
        Item item = addItem("item5", "description5", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        State state = State.WAITING;
        service.save(bookingDto, booker.getId());
        List<BookingResponseDto> bookings = service.findByStateUser(booker.getId(), state);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(bookings.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookings.get(0).getBooker().getName(), equalTo(booker.getName()));
        assertThat(bookings.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.WAITING));
    }

    @Test
    public void findByStateOwnerWAITING() {
        User owner = addUser("user11", "email11@mail.ru");
        User booker = addUser("user12", "email12@mail.ru");
        Item item = addItem("item6", "description6", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        State state = State.WAITING;
        service.save(bookingDto, booker.getId());
        List<BookingResponseDto> bookings = service.findByStateOwner(owner.getId(), state);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(bookings.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookings.get(0).getBooker().getName(), equalTo(booker.getName()));
        assertThat(bookings.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.WAITING));
    }

    @Test
    public void findByStateUserREJECTED() {
        User owner = addUser("user13", "email13@mail.ru");
        User booker = addUser("user14", "email14@mail.ru");
        Item item = addItem("item7", "description5", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        State state = State.REJECTED;
        Boolean isApproved = false;
        service.save(bookingDto, booker.getId());
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b " +
                "where b.booker.id = :bookerId", Booking.class);
        Booking booking = query.setParameter("bookerId", booker.getId()).getSingleResult();
        service.update(owner.getId(), isApproved, booking.getId());
        List<BookingResponseDto> bookings = service.findByStateUser(booker.getId(), state);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(bookings.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookings.get(0).getBooker().getName(), equalTo(booker.getName()));
        assertThat(bookings.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    public void findByStateOwnerREJECTED() {
        User owner = addUser("user15", "email15@mail.ru");
        User booker = addUser("user16", "email16@mail.ru");
        Item item = addItem("item8", "description8", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        State state = State.REJECTED;
        Boolean isApproved = false;
        service.save(bookingDto, booker.getId());
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b " +
                "where b.item.owner.id = :ownerId", Booking.class);
        Booking booking = query.setParameter("ownerId", owner.getId()).getSingleResult();
        service.update(owner.getId(), isApproved, booking.getId());
        List<BookingResponseDto> bookings = service.findByStateOwner(owner.getId(), state);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(bookings.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookings.get(0).getBooker().getName(), equalTo(booker.getName()));
        assertThat(bookings.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    public void findByStateUserCURRENT() {
        User owner = addUser("user17", "email17@mail.ru");
        User booker = addUser("user18", "email18@mail.ru");
        Item item = addItem("item9", "description5", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        bookingDto.setStart(LocalDateTime.parse("2023-01-02T12:00:00"));
        bookingDto.setEnd(LocalDateTime.parse("2023-12-02T12:00:00"));
        State state = State.CURRENT;
        service.save(bookingDto, booker.getId());
        List<BookingResponseDto> bookings = service.findByStateUser(booker.getId(), state);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(bookings.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookings.get(0).getBooker().getName(), equalTo(booker.getName()));
        assertThat(bookings.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.WAITING));
    }

    @Test
    public void findByStateOwnerCURRENT() {
        User owner = addUser("user19", "email19@mail.ru");
        User booker = addUser("user20", "email20@mail.ru");
        Item item = addItem("item10", "description6", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        bookingDto.setStart(LocalDateTime.parse("2023-01-02T12:00:00"));
        bookingDto.setEnd(LocalDateTime.parse("2023-12-02T12:00:00"));
        State state = State.CURRENT;
        service.save(bookingDto, booker.getId());
        List<BookingResponseDto> bookings = service.findByStateOwner(owner.getId(), state);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(bookings.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookings.get(0).getBooker().getName(), equalTo(booker.getName()));
        assertThat(bookings.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.WAITING));
    }

    @Test
    public void findByStateUserPAST() {
        User owner = addUser("user21", "email21@mail.ru");
        User booker = addUser("user22", "email22@mail.ru");
        Item item = addItem("item11", "description5", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        bookingDto.setStart(LocalDateTime.parse("2023-01-02T12:00:00"));
        bookingDto.setEnd(LocalDateTime.parse("2023-02-02T12:00:00"));
        State state = State.PAST;
        Boolean isApproved = true;
        service.save(bookingDto, booker.getId());
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b " +
                "where b.booker.id = :bookerId", Booking.class);
        Booking booking = query.setParameter("bookerId", booker.getId()).getSingleResult();
        service.update(owner.getId(), isApproved, booking.getId());
        List<BookingResponseDto> bookings = service.findByStateUser(booker.getId(), state);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(bookings.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookings.get(0).getBooker().getName(), equalTo(booker.getName()));
        assertThat(bookings.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    public void findByStateOwnerPAST() {
        User owner = addUser("user23", "email23@mail.ru");
        User booker = addUser("user24", "email24@mail.ru");
        Item item = addItem("item12", "description12", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        bookingDto.setStart(LocalDateTime.parse("2023-01-02T12:00:00"));
        bookingDto.setEnd(LocalDateTime.parse("2023-02-02T12:00:00"));
        State state = State.PAST;
        Boolean isApproved = true;
        service.save(bookingDto, booker.getId());
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b " +
                "where b.booker.id = :bookerId", Booking.class);
        Booking booking = query.setParameter("bookerId", booker.getId()).getSingleResult();
        service.update(owner.getId(), isApproved, booking.getId());
        List<BookingResponseDto> bookings = service.findByStateOwner(owner.getId(), state);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(bookings.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookings.get(0).getBooker().getName(), equalTo(booker.getName()));
        assertThat(bookings.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    public void findByStateUserFUTURE() {
        User owner = addUser("user25", "email25@mail.ru");
        User booker = addUser("user26", "email26@mail.ru");
        Item item = addItem("item13", "description13", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        bookingDto.setStart(LocalDateTime.parse("2023-10-02T12:00:00"));
        bookingDto.setEnd(LocalDateTime.parse("2023-12-02T12:00:00"));
        State state = State.FUTURE;
        service.save(bookingDto, booker.getId());
        List<BookingResponseDto> bookings = service.findByStateUser(booker.getId(), state);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(bookings.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookings.get(0).getBooker().getName(), equalTo(booker.getName()));
        assertThat(bookings.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.WAITING));
    }

    @Test
    public void findByStateOwnerFUTURE() {
        User owner = addUser("user27", "email27@mail.ru");
        User booker = addUser("user28", "email28@mail.ru");
        Item item = addItem("item14", "description14", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        bookingDto.setStart(LocalDateTime.parse("2023-10-02T12:00:00"));
        bookingDto.setEnd(LocalDateTime.parse("2023-12-02T12:00:00"));
        State state = State.FUTURE;
        service.save(bookingDto, booker.getId());
        List<BookingResponseDto> bookings = service.findByStateOwner(owner.getId(), state);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(bookings.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookings.get(0).getBooker().getName(), equalTo(booker.getName()));
        assertThat(bookings.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.WAITING));
    }

    @Test
    public void getById() {
        User owner = addUser("user29", "email29@mail.ru");
        User booker = addUser("user30", "email30@mail.ru");
        Item item = addItem("item15", "description15", owner.getId());
        BookingDto bookingDto = makeBookingDto(item.getId());
        service.save(bookingDto, booker.getId());
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b " +
                "where b.booker.id = :bookerId", Booking.class);
        Booking booking = query.setParameter("bookerId", booker.getId()).getSingleResult();
        BookingResponseDto checkBooking = service.getById(owner.getId(), booking.getId());
        assertThat(checkBooking.getId(), equalTo(booking.getId()));
        assertThat(checkBooking.getItem().getId(), equalTo(item.getId()));
        assertThat(checkBooking.getItem().getName(), equalTo(item.getName()));
        assertThat(checkBooking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(checkBooking.getBooker().getName(), equalTo(booker.getName()));
        assertThat(checkBooking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(checkBooking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(checkBooking.getStatus(), equalTo(Status.WAITING));
    }

    private BookingDto makeBookingDto(Integer itemId) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now());
        bookingDto.setItemId(itemId);
        return bookingDto;
    }

    private User addUser(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        userService.save(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        return query.setParameter("email", userDto.getEmail())
                .getSingleResult();
    }

    private Item addItem(String name, String description, Integer userId) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);
        itemDto.setRequestId(1);
        itemService.save(itemDto, userId);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        return query.setParameter("name", itemDto.getName())
                .getSingleResult();
    }
}
