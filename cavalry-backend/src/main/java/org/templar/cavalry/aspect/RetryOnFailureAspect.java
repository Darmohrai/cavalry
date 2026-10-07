package org.templar.cavalry.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.templar.cavalry.annotation.RetryOnFailure;

@Aspect
@Component
@Slf4j
public class RetryOnFailureAspect {

    @Around("@annotation(retryOnFailure)")
    public Object retryOperation(ProceedingJoinPoint joinPoint, RetryOnFailure retryOnFailure) throws Throwable {
        int attempts = 0;
        Throwable lastException = null;

        while (attempts < retryOnFailure.maxAttempts()) {
            attempts++;
            try {
                return joinPoint.proceed();
            } catch (Throwable ex) {
                lastException = ex;
                log.warn("AOP Retry: Attempt {}/{} failed for {}. Retrying in {} ms...",
                        attempts, retryOnFailure.maxAttempts(),
                        joinPoint.getSignature().toShortString(), retryOnFailure.delayMs());

                if (attempts < retryOnFailure.maxAttempts()) {
                    Thread.sleep(retryOnFailure.delayMs());
                }
            }
        }

        log.error("AOP Retry: All {} attempts failed for {}",
                retryOnFailure.maxAttempts(), joinPoint.getSignature().toShortString());

        throw lastException;
    }
}
