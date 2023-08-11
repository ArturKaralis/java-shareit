package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.LastBooking;
import ru.practicum.shareit.booking.model.NextBooking;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ItemResponseDto {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;

    private LastBooking lastBooking;
    private NextBooking nextBooking;

    private List<CommentResponseDto> comments;

}
