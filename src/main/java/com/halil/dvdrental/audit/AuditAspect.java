package com.halil.dvdrental.audit;

import com.halil.dvdrental.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Order(0)
@Slf4j
public class AuditAspect {

    @Around("@annotation(auditLog)")
    public Object audit(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {

        String action = auditLog.value();
        String method = joinPoint.getSignature().getDeclaringType().getSimpleName()
                + "." + joinPoint.getSignature().getName();

        log.debug("AuditAspect devrede: {}", method);

        long start = System.nanoTime();
        Object result;

        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            logFailure(action, method, joinPoint.getArgs(), ex, start);
            throw ex;
        }

        logSuccess(action, method, joinPoint.getArgs(), result, start);
        return result;
    }

    private void logSuccess(String action, String method, Object[] args, Object result, long start) {
        try {
            log.info("İŞLEM | {} | {} | Kullanıcı: {} | Args: {} | Sonuç: {} | {} ms",
                    action, method, SecurityUtils.getCurrentUserFullName(),
                    describeArgs(args), describeResult(result), elapsedMs(start));
        } catch (Exception e) {
            log.error("Audit log yazılamadı", e);
        }
    }

    private void logFailure(String action, String method, Object[] args, Throwable ex, long start) {
        try {
            log.warn("İŞLEM | {} | {} | Kullanıcı: {} | Args: {} | HATA: {} | {} ms",
                    action, method, SecurityUtils.getCurrentUserFullName(),
                    describeArgs(args), ex.getClass().getSimpleName(), elapsedMs(start));
        } catch (Exception e) {
            log.error("Audit log yazılamadı", e);
        }
    }

    private long elapsedMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }

    private String describeArgs(Object[] args) {
        return Arrays.stream(args)
                .map(a -> {
                    if (a == null) return "null";
                    if (a instanceof Number || a instanceof Boolean) return String.valueOf(a);
                    return "<" + a.getClass().getSimpleName() + ">";
                })
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private String describeResult(Object result) {
        if (result == null) return "null";
        if (result instanceof Boolean) return result.toString();
        if (result instanceof Enum<?> e) return e.name();
        return "OK";
    }
}