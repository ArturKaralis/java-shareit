package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.NextBooking;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NexBookingMapper {

    public static NextBooking bookingToNexBooking(Booking booking) {
        NextBooking nextBooking = new NextBooking();
        nextBooking.setId(booking.getId());
        nextBooking.setBookerId(booking.getBooker().getId());
        return nextBooking;
    }
}
