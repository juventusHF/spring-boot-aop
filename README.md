# AOP mit Spring Boot

Inspiziere `pom.xml` um zu sehen, wie Spring AOP über einen Spring-Boot-Starter eingebunden wird.

Starte die Applikation:

    mvn spring-boot:run

Du kannst nun die Applikation unter folgenden URLs aufrufen:
- http://localhost:8080/employees
- http://localhost:8080/departments

## cURL

Um mit dieser Übung arbeiten zu können, solltest Du cURL installieren.

- macOS: `brew install curl`
- Windows: https://stackoverflow.com/a/16216825/5155817

Hinweis: Mit `curl -v ...` werden die Details der Kommunikation via HTTP angezeigt.

## Aufgaben

### Erzeuge und benutze den Auditlogger

Erzeuge die folgende Annotation:

    package ch.juventus.example.logging;

    import java.lang.annotation.*;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface AuditLog {    }

Erzeuge im gleichen Java-Package den zunächst leeren `AuditLogger`-Aspekt (welcher bereits einen "named Pointcut" enthält):

    @Aspect
    @Configuration
    public class AuditLogger {

        private static Logger logger = LoggerFactory.getLogger(AuditLogger.class);

        @Pointcut("execution(public * *(..)) && @within(ch.juventus.example.logging.AuditLog)")
        public void auditLogMethods() {}
    }

Benutze nun die Annotation auf dem `EmployeeController` und dem `DepartmentController`:

    @AuditLog
    @RestController
    public class DepartmentController {
        ...
    }

### Implentiere den Advice

Im `AuditLogger`-Aspekt können nun die Advice-Implementierungen abgelegt werden. Implementiere den "Before"-Advice:

    @Before("auditLogMethods()")
    public void enterMethod(JoinPoint joinPoint) {
        String operation = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.info("Entering {} with parameters {}.", operation, args);
    }

Implementiere nun die beiden "After*"-Advices:

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

Im `AfterThrowing`-Advice ist es möglich, die auftretende Exception als Methodenargument zu binden und damit in der Implementierung zu benutzen.

### Teste den Aspekt

Sende einige Requests zum Service und verifiziere, sodass der Advice in STDOUT (Console) loggt:

    curl -v http://localhost:8080/departments

    curl -v http://localhost:8080/employees/3

    curl -v -H "Content-Type: application/json" -d \
    '{"firstName":"Heidi","lastName":"Keppert","_links":{"department":{"href":"http://localhost:8080/departments/1"}}}' \
    http://localhost:8080/employees

### Inspiziere und adaptiere Logback

Die Konfiguration für Logback befindet sich in `src/main/resources/logback.xml`. Füge einen Logger für `com.springframework.aop` hinzu und starte die Applikation neu.
