package ru.practicum.shareit.validator;

import ru.practicum.shareit.booking.dto.CreateBookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, CreateBookingDto> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(CreateBookingDto createBookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = createBookingDto.getStart();
        LocalDateTime end = createBookingDto.getEnd();
        if (end.isBefore(start) || end.equals(start)) {
            return false;
        }
        return start.isBefore(end);
    }
}
