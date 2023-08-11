package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartEndConstraintValidator implements ConstraintValidator<EndAfterStart, BookingDto> {

    @Override
    public void initialize(EndAfterStart endAfterStart) {
    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext cxt) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            return false;
        }
        return bookingDto.getEnd().isAfter(bookingDto.getStart());
    }
}
