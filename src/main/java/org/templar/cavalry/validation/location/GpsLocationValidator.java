package org.templar.cavalry.validation.location;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GpsLocationValidator implements ConstraintValidator<ValidGpsLocation, String> {

    @Override
    public boolean isValid(String location, ConstraintValidatorContext context) {
        if (location == null || location.isBlank()) {
            return true;
        }

        String[] parts = location.split(",");
        if (parts.length != 2) {
            setCustomMessage(context, "Локація має бути у форматі 'широта, довгота' (наприклад: '48.29, 25.93')");
            return false;
        }

        try {
            double lat = Double.parseDouble(parts[0].trim());
            double lon = Double.parseDouble(parts[1].trim());

            if (lat < -90.0 || lat > 90.0) {
                setCustomMessage(context, String.format("Широта '%s' поза межами [-90, 90]", parts[0].trim()));
                return false;
            }
            if (lon < -180.0 || lon > 180.0) {
                setCustomMessage(context, String.format("Довгота '%s' поза межами [-180, 180]", parts[1].trim()));
                return false;
            }

            return true;

        } catch (NumberFormatException e) {
            setCustomMessage(context, "Координати мають бути числовими значеннями");
            return false;
        }
    }

    private void setCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
