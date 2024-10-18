package ru.company.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Constraint(validatedBy = ReleaseDateValidationImpl.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReleaseDateValidation {

    String dateStart();

    String message() default "ru.company.filmorate.annotation.ReleaseDateValidation";

    //TODO посмотри
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
