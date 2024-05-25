package com.hoangtien2k3.shopappbackend.components.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
@Aspect
public class UserActivityLogger {
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {
    }

    @Around("controllerMethods() && execution(* com.hoangtien2k3.shopappbackend.controllers.UserController.*(..))")
    public Object logUserActivity(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String remoteAddress = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getRemoteAddr();
        log.info("User activity started: {}, IP address: {}", methodName, remoteAddress);
        // thực hiện method gốc
        Object result = joinPoint.proceed();
        // ghi log sau khi thực hiện method
        log.info("User activity finished: {}, IP address: {}", methodName, remoteAddress);
        return result;
    }
}
