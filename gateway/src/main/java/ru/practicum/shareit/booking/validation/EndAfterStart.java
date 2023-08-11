package ru.practicum.shareit.booking.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StartEndConstraintValidator.class)
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EndAfterStart {

    String message() default "{End can not be before Start}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
