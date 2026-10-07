package org.templar.cavalry.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.templar.cavalry.annotation.ExecutionMonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExecutionMonitorAspectTest {

    @InjectMocks
    private ExecutionMonitorAspect aspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private ExecutionMonitor annotation;

    @Mock
    private MethodSignature signature;

    @Test
    void whenExecutionIsFast_thenDoNotLogAndReturnResult() throws Throwable {
        when(annotation.thresholdMs()).thenReturn(10000L);
        when(joinPoint.proceed()).thenReturn("FAST_RESULT");

        Object result = aspect.monitorExecutionTime(joinPoint, annotation);

        assertEquals("FAST_RESULT", result);
        verify(joinPoint, times(1)).proceed();

        verify(joinPoint, never()).getSignature();
    }

    @Test
    void whenExecutionIsSlow_thenLogAndReturnResult() throws Throwable {
        when(annotation.thresholdMs()).thenReturn(0L);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("TestClass.testMethod()");

        when(joinPoint.proceed()).thenAnswer(invocation -> {
            Thread.sleep(10);
            return "SLOW_RESULT";
        });

        Object result = aspect.monitorExecutionTime(joinPoint, annotation);

        assertEquals("SLOW_RESULT", result);
        verify(joinPoint, times(1)).proceed();

        verify(joinPoint, times(1)).getSignature();
    }
}