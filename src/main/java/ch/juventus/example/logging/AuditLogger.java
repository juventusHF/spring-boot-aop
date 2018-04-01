package ch.juventus.example.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class AuditLogger {

    private static Logger logger = LoggerFactory.getLogger(AuditLogger.class);

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {}

    @Before("publicMethod() && @within(ch.juventus.example.logging.AuditLog)")
    public void enterMethod(JoinPoint joinPoint) {
        String operation = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.info("Entering {} with parameters {}.", operation, args);
    }
}
