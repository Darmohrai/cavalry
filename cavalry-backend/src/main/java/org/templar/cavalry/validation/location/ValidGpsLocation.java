package org.templar.cavalry.validation.location;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GpsLocationValidator.class)
@Documented
public @interface ValidGpsLocation {

    String message() default "Incorrect GPS coordinates";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
