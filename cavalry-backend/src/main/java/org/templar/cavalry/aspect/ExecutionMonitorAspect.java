package org.templar.cavalry.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.templar.cavalry.annotation.ExecutionMonitor;

@Aspect
@Component
@Slf4j
public class ExecutionMonitorAspect {

    @Around("@annotation(executionMonitor)")
    public Object monitorExecutionTime(ProceedingJoinPoint joinPoint, ExecutionMonitor executionMonitor) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        if (executionTime >= executionMonitor.thresholdMs()) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            log.warn("=== AOP MONITOR === Method {} took {} ms to execute (Limit: {} ms)",
                    signature.toShortString(), executionTime, executionMonitor.thresholdMs());
        }

        return result;
    }
}
