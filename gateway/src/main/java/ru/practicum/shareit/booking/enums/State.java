package ru.practicum.shareit.booking.enums;

import ru.practicum.shareit.exception.BookingStatusException;

public enum State {

    WAITING,
    APPROVED,
    REJECTED,
    PAST,
    CURRENT,
    FUTURE,
    ALL;

    public static State from(String stateString) {
        State state;
        try {
            state = State.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new BookingStatusException("Unknown state: " + stateString);
        }
        return state;
    }

}
