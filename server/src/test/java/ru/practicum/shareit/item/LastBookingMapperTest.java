package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.LastBooking;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class LastBookingMapperTest {

    @Test
    public void bookingToLastBooking() {
        User booker = new User();
        booker.setId(1);
        Booking booking = new Booking();
        booking.setId(1);
        booking.setBooker(booker);
        LastBooking lastBooking = LastBookingMapper.bookingToLastBooking(booking);
        assertThat(lastBooking.getId(), equalTo(1));
        assertThat(lastBooking.getBookerId(), equalTo(booker.getId()));
    }
}
