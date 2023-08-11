package ru.practicum.shareit.validation;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.validation.StartEndConstraintValidator;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StartEndConstraintValidatorTest {

    private final StartEndConstraintValidator validator = new StartEndConstraintValidator();

    private final BookingDto bookingDto = new BookingDto();

    private ConstraintValidatorContext cxt;

    @Test
    public void isValidTrue() {
        bookingDto.setStart(LocalDateTime.parse("2023-02-03T12:13:14"));
        bookingDto.setEnd(LocalDateTime.parse("2023-03-04T12:13:14"));
        Boolean isValid = validator.isValid(bookingDto, cxt);
        assertEquals(true, isValid);
    }

    @Test
    public void isValidFalse() {
        bookingDto.setStart(LocalDateTime.parse("2023-02-03T12:13:14"));
        bookingDto.setEnd(LocalDateTime.parse("2023-01-04T12:13:14"));
        Boolean isValid = validator.isValid(bookingDto, cxt);
        assertEquals(false, isValid);
    }
}
