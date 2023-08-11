package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.data.domain.Sort.Direction.ASC;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private Booking bookingPresent;
    private Booking bookingPast;
    private Booking bookingFuture;

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @BeforeEach
    public void saveEntity() {
        if (user == null) {
            user = new User();
            user.setName("userName");
            user.setEmail("user@mail.ru");
            userRepository.save(user);
        }

        if (item == null) {
            item = new Item();
            item.setName("itemName");
            item.setDescription("description");
            item.setAvailable(true);
            item.setOwner(user);
            itemRepository.save(item);
        }

        if (bookingPresent == null) {
            bookingPresent = new Booking();
            bookingPresent.setBooker(user);
            bookingPresent.setItem(item);
            bookingPresent.setStatus(Status.APPROVED);
            bookingPresent.setStart(LocalDateTime.parse("2023-01-02T12:13:14"));
            bookingPresent.setEnd(LocalDateTime.parse("2023-04-02T12:13:14"));
            bookingRepository.save(bookingPresent);
        }

        if (bookingPast == null) {
            bookingPast = new Booking();
            bookingPast.setBooker(user);
            bookingPast.setItem(item);
            bookingPast.setStatus(Status.APPROVED);
            bookingPast.setStart(LocalDateTime.parse("2023-01-02T12:13:14"));
            bookingPast.setEnd(LocalDateTime.parse("2023-02-02T12:13:14"));
            bookingRepository.save(bookingPast);
        }

        if (bookingFuture == null) {
            bookingFuture = new Booking();
            bookingFuture.setBooker(user);
            bookingFuture.setItem(item);
            bookingFuture.setStatus(Status.APPROVED);
            bookingFuture.setStart(LocalDateTime.parse("2023-04-02T12:13:14"));
            bookingFuture.setEnd(LocalDateTime.parse("2023-05-02T12:13:14"));
            bookingRepository.save(bookingFuture);
        }
    }

    @Test
    public void findByBookerId() {
        List<Booking> bookings = bookingRepository
                .findByBookerId(user.getId(), PageRequest.of(0, 10)).getContent();
        assertEquals(3, bookings.size());
        assertNotNull(bookings.get(0).getId());
        assertEquals(bookingPresent.getStart(), bookings.get(0).getStart());
        assertEquals(bookingPresent.getEnd(), bookings.get(0).getEnd());
        assertEquals("APPROVED", bookings.get(0).getStatus().toString());
        assertNotNull(bookings.get(0).getBooker().getId());
        assertNotNull(bookings.get(0).getItem().getId());
    }

    @Test
    public void findByOwnerId() {
        List<Booking> bookings = bookingRepository
                .findByOwnerId(item.getOwner().getId(), PageRequest.of(0, 10)).getContent();
        assertEquals(3, bookings.size());
        assertNotNull(bookings.get(0).getId());
        assertEquals(bookingPresent.getStart(), bookings.get(0).getStart());
        assertEquals(bookingPresent.getEnd(), bookings.get(0).getEnd());
        assertEquals("APPROVED", bookings.get(0).getStatus().toString());
        assertNotNull(bookings.get(0).getBooker().getId());
        assertNotNull(bookings.get(0).getItem().getId());
    }

    @Test
    public void findCurrentBookingUser() {
        List<Booking> bookings = bookingRepository
                .findCurrentBookingUser(LocalDateTime.parse("2023-03-10T12:13:14"), user.getId());
        assertEquals(1, bookings.size());
        assertNotNull(bookings.get(0).getId());
        assertEquals(bookingPresent.getStart(), bookings.get(0).getStart());
        assertEquals(bookingPresent.getEnd(), bookings.get(0).getEnd());
        assertEquals("APPROVED", bookings.get(0).getStatus().toString());
        assertNotNull(bookings.get(0).getBooker().getId());
        assertNotNull(bookings.get(0).getItem().getId());
    }

    @Test
    public void findCurrentBookingOwner() {
        List<Booking> bookings = bookingRepository
                .findCurrentBookingOwner(LocalDateTime.parse("2023-03-10T12:13:14"), item.getOwner().getId());
        assertEquals(1, bookings.size());
        assertNotNull(bookings.get(0).getId());
        assertEquals(bookingPresent.getStart(), bookings.get(0).getStart());
        assertEquals(bookingPresent.getEnd(), bookings.get(0).getEnd());
        assertEquals("APPROVED", bookings.get(0).getStatus().toString());
        assertNotNull(bookings.get(0).getBooker().getId());
        assertNotNull(bookings.get(0).getItem().getId());
    }

    @Test
    public void findPastBookingUser() {
        List<Booking> bookings = bookingRepository
                .findPastBookingUser(LocalDateTime.parse("2023-03-10T12:13:14"),
                        user.getId(), Sort.by(ASC, "start"));
        assertEquals(1, bookings.size());
        assertNotNull(bookings.get(0).getId());
        assertEquals(bookingPast.getStart(), bookings.get(0).getStart());
        assertEquals(bookingPast.getEnd(), bookings.get(0).getEnd());
        assertEquals("APPROVED", bookings.get(0).getStatus().toString());
        assertNotNull(bookings.get(0).getBooker().getId());
        assertNotNull(bookings.get(0).getItem().getId());
    }

    @Test
    public void findPastBookingByItemId() {
        List<Booking> bookings = bookingRepository
                .findPastBookingByItemId(LocalDateTime.parse("2023-03-10T12:13:14"), item.getId());
        assertEquals(2, bookings.size());
        assertNotNull(bookings.get(0).getId());
        assertEquals(bookingPresent.getStart(), bookings.get(0).getStart());
        assertEquals(bookingPresent.getEnd(), bookings.get(0).getEnd());
        assertEquals("APPROVED", bookings.get(0).getStatus().toString());
        assertNotNull(bookings.get(0).getBooker().getId());
        assertNotNull(bookings.get(0).getItem().getId());
    }

    @Test
    public void findPastBookingOwner() {
        List<Booking> bookings = bookingRepository
                .findPastBookingOwner(LocalDateTime.parse("2023-03-10T12:13:14"),
                        item.getOwner().getId(), Sort.by(ASC, "start"));
        assertEquals(2, bookings.size());
        assertNotNull(bookings.get(0).getId());
        assertEquals(bookingPresent.getStart(), bookings.get(0).getStart());
        assertEquals(bookingPresent.getEnd(), bookings.get(0).getEnd());
        assertEquals("APPROVED", bookings.get(0).getStatus().toString());
        assertNotNull(bookings.get(0).getBooker().getId());
        assertNotNull(bookings.get(0).getItem().getId());
    }

    @Test
    public void findFutureBookingUser() {
        List<Booking> bookings = bookingRepository
                .findFutureBookingUser(LocalDateTime.parse("2023-03-10T12:13:14"),
                        user.getId(), Sort.by(ASC, "start"));
        assertEquals(1, bookings.size());
        assertNotNull(bookings.get(0).getId());
        assertEquals(bookingFuture.getStart(), bookings.get(0).getStart());
        assertEquals(bookingFuture.getEnd(), bookings.get(0).getEnd());
        assertEquals("APPROVED", bookings.get(0).getStatus().toString());
        assertNotNull(bookings.get(0).getBooker().getId());
        assertNotNull(bookings.get(0).getItem().getId());
    }

    @Test
    public void findFutureBookingOwner() {
        List<Booking> bookings = bookingRepository
                .findFutureBookingOwner(LocalDateTime.parse("2023-03-10T12:13:14"),
                        item.getOwner().getId(), Sort.by(ASC, "start"));
        assertEquals(1, bookings.size());
        assertNotNull(bookings.get(0).getId());
        assertEquals(bookingFuture.getStart(), bookings.get(0).getStart());
        assertEquals(bookingFuture.getEnd(), bookings.get(0).getEnd());
        assertEquals("APPROVED", bookings.get(0).getStatus().toString());
        assertNotNull(bookings.get(0).getBooker().getId());
        assertNotNull(bookings.get(0).getItem().getId());
    }

    @Test
    public void findFutureBookingOwnerApproved() {
        List<Booking> bookings = bookingRepository
                .findFutureBookingOwnerApproved(LocalDateTime.parse("2023-03-10T12:13:14"),
                        item.getOwner().getId(), Sort.by(ASC, "start"));
        assertEquals(1, bookings.size());
        assertNotNull(bookings.get(0).getId());
        assertEquals(bookingFuture.getStart(), bookings.get(0).getStart());
        assertEquals(bookingFuture.getEnd(), bookings.get(0).getEnd());
        assertEquals("APPROVED", bookings.get(0).getStatus().toString());
        assertNotNull(bookings.get(0).getBooker().getId());
        assertNotNull(bookings.get(0).getItem().getId());
    }

    @Test
    public void findByStatusAndBookerId() {
        List<Booking> bookings = bookingRepository.findByStatusAndBookerId(
                Status.APPROVED, user.getId(), Sort.by(ASC, "start"));
        assertEquals(3, bookings.size());
        assertNotNull(bookings.get(0).getId());
        assertEquals(bookingPresent.getStart(), bookings.get(0).getStart());
        assertEquals(bookingPresent.getEnd(), bookings.get(0).getEnd());
        assertEquals("APPROVED", bookings.get(0).getStatus().toString());
        assertNotNull(bookings.get(0).getBooker().getId());
        assertNotNull(bookings.get(0).getItem().getId());
    }

    @Test
    public void findByStatusAndOwnerId() {
        List<Booking> bookings = bookingRepository.findByStatusAndOwnerId(
                Status.APPROVED, item.getOwner().getId(), Sort.by(ASC, "start"));
        assertEquals(3, bookings.size());
        assertNotNull(bookings.get(0).getId());
        assertEquals(bookingPresent.getStart(), bookings.get(0).getStart());
        assertEquals(bookingPresent.getEnd(), bookings.get(0).getEnd());
        assertEquals("APPROVED", bookings.get(0).getStatus().toString());
        assertNotNull(bookings.get(0).getBooker().getId());
        assertNotNull(bookings.get(0).getItem().getId());
    }

}
