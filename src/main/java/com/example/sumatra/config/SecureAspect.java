package com.example.sumatra.config;

import com.example.sumatra.controller.Secured;
import com.example.sumatra.filter.JwtContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class SecureAspect {

    @Pointcut("execution ( * com.example.sumatra.controller.*.*(..))")
    public void securedArgs() {
    }


    @Before(" securedArgs()")
    public void checkUserId(JoinPoint joinPoint) {
        long id = JwtContextHolder.getClaims().get("user_id", Long.class);
        log.info("claim user id: {}", id);
        for (Object arg : joinPoint.getArgs()) {
            if (arg != null && arg.getClass().isAnnotationPresent(Secured.class)) {
                Secured secured = arg.getClass().getAnnotation(Secured.class);
                if (id != (long) arg) {
                    throw new RuntimeException("Access denied");
                }
            }
        }
    }
}
