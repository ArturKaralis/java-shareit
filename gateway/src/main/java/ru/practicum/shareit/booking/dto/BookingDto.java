package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.validation.EndAfterStart;
import ru.practicum.shareit.utility.Create;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EndAfterStart(groups = {Create.class})
public class BookingDto {

    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;
    private LocalDateTime end;
    @NotNull(groups = {Create.class})
    private Integer itemId;

}
