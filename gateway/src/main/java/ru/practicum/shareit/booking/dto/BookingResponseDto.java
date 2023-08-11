package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BookingResponseDto {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private Booker booker;
    private Item item;

    public BookingResponseDto(Integer bookerId, String bookerName, Integer itemId, String itemName) {
        this.booker = new Booker(bookerId, bookerName);
        this.item = new Item(itemId, itemName);
    }

    @Data
    public static class Booker {
        private final Integer id;
        private final String name;
    }

    @Data
    public static class Item {
        private final Integer id;
        private final String name;
    }

}
