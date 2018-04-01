package ch.juventus.example.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Aspect @Configuration
public class AuditLogger {

    private static Logger logger = LoggerFactory.getLogger(AuditLogger.class);

    @Pointcut("execution(public * *(..)) && @within(ch.juventus.example.logging.AuditLog)")
    public void auditLogMethods() {}

    @Before("auditLogMethods()")
    public void enterMethod(JoinPoint joinPoint) {
        String operation = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.info("Entering {} with parameters {}.", operation, args);
    }

    @AfterReturning(pointcut = "auditLogMethods()", returning = "returnValue")
    public void leaveMethodWithReturnvalue(JoinPoint joinPoint, Object returnValue) {
        String operation = joinPoint.getSignature().getName();
        logger.info("Leaving {} with return value {}.", operation, returnValue);
    }

    @AfterThrowing(pointcut = "auditLogMethods()", throwing = "throwable")
    public void leaveMethodWithException(JoinPoint joinPoint, Throwable throwable) {
        String operation = joinPoint.getSignature().getName();
        logger.info("Leaving {} with exception {}.", operation, throwable);
    }
}
