package ru.company.filmorate.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReleaseDateValidationImpl implements ConstraintValidator<ReleaseDateValidation, LocalDate> {

    private String dateStart;

    @Override
    public void initialize(ReleaseDateValidation constraintAnnotation) {
        dateStart = constraintAnnotation.dateStart();
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(LocalDate.parse(dateStart, DateTimeFormatter.ofPattern("yyyy.MM.dd"))); // TODO
    }
}
