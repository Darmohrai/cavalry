package org.templar.cavalry.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.templar.cavalry.annotation.MaskLocation;
import org.templar.cavalry.dto.StationDto;

import java.util.List;

@Aspect
@Component
@Slf4j
public class MaskLocationAspect {

    @Around("@annotation(maskLocation)")
    public Object maskStationLocation(ProceedingJoinPoint joinPoint, MaskLocation maskLocation) throws Throwable {
        Object result = joinPoint.proceed();

        if (result == null) {
            return null;
        }

        log.debug("AOP Masking: Recomputing result with masked location '{}'", maskLocation.maskString());

        if (result instanceof StationDto station) {
            return maskStation(station, maskLocation.maskString());
        }
        else if (result instanceof List<?> list) {
            return list.stream()
                    .map(item -> {
                        if (item instanceof StationDto station) {
                            return maskStation(station, maskLocation.maskString());
                        }
                        return item;
                    })
                    .toList();
        }

        return result;
    }

    private StationDto maskStation(StationDto original, String mask) {
        return new StationDto(
                original.id(),
                original.name(),
                mask,
                original.status(),
                original.createdAt()
        );
    }
}