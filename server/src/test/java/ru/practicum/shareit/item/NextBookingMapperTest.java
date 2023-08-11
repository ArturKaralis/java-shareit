package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.NextBooking;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class NextBookingMapperTest {

    @Test
    public void bookingToNexBooking() {
        User booker = new User();
        booker.setId(1);
        Booking booking = new Booking();
        booking.setId(1);
        booking.setBooker(booker);
        NextBooking nextBooking = NexBookingMapper.bookingToNexBooking(booking);
        assertThat(nextBooking.getId(), equalTo(1));
        assertThat(nextBooking.getBookerId(), equalTo(booker.getId()));
    }
}
