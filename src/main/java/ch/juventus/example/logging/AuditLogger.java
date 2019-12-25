package ch.juventus.example.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class AuditLogger {

    private static Logger logger = LoggerFactory.getLogger(AuditLogger.class);

    @Pointcut("execution(public * *(..)) && @within(ch.juventus.example.logging.AuditLog)")
    public void auditLogMethods() {

    }

    @Before("auditLogMethods()")
    public void enterMethod(JoinPoint joinPoint) {
        String operation = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.info("Entering {} with parameters {}.", operation, args);
    }

    @AfterReturning(pointcut = "auditLogMethods()")
    public void leaveMethodWithReturnvalue(JoinPoint joinPoint) {
        String operation = joinPoint.getSignature().getName();
        logger.info("Leaving {}.", operation);
    }

    @AfterThrowing(pointcut = "auditLogMethods()", throwing = "throwable")
    public void leaveMethodWithException(JoinPoint joinPoint, Throwable throwable) {
        String operation = joinPoint.getSignature().getName();
        logger.info("Leaving {} with exception {}.", operation, throwable);
    }
}
