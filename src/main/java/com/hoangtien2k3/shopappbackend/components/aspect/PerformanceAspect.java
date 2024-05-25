package com.hoangtien2k3.shopappbackend.components.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class PerformanceAspect {
    @Pointcut("within(com.hoangtien2k3.shopappbackend.controllers.*)")
    public void controllerMethos() {
    }

    private String getMethodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }

    // log ra tên method trước khi thực hiện
    @Before("controllerMethos()")
    public void beforeControllerExcution(JoinPoint joinPoint) {
        log.info("Starting execution of: {}", getMethodName(joinPoint));
    }

    // log ra tên method sau khi thực hiện
    @After("controllerMethos()")
    public void afterControllerExcution(JoinPoint joinPoint) {
        log.info("Finished execution of: {}", getMethodName(joinPoint));
    }

    // tính xem method đó thực hiện hết bao nhiêu giây
    @Around("controllerMethos()")
    public Object aroundControllerExcution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime(); // thời gian bắt đầu
        // thực hiện method
        Object result = joinPoint.proceed();
        long endTime = System.nanoTime(); // thời gian kết thúc
        String methodName = getMethodName(joinPoint);
        log.info("=> Execution of {} took {} ms", methodName, TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
        return result;
    }
}
