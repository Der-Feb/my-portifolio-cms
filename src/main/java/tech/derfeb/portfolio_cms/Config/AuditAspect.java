package tech.derfeb.portfolio_cms.Config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import tech.derfeb.portfolio_cms.Service.AuditLogService;

import java.lang.reflect.Method;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogService auditLogService;

    @Pointcut("execution(* tech.derfeb.portfolio_cms.Service.*.create*(..)) || " +
              "execution(* tech.derfeb.portfolio_cms.Service.*.update*(..)) || " +
              "execution(* tech.derfeb.portfolio_cms.Service.*.delete*(..))")
    public void auditMethods() {}

    @AfterReturning(pointcut = "auditMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String action = getActionFromMethodName(methodName);
        String entityType = className.replace("Service", "");
        
        String performedBy = "SYSTEM";
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            performedBy = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        String entityId = "N/A";
        
        if (action.equals("DELETE")) {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof String) {
                entityId = (String) args[0];
            }
        } else if (result != null) {
            try {
                Method getIdMethod = result.getClass().getMethod("getId");
                Object id = getIdMethod.invoke(result);
                if (id != null) {
                    entityId = id.toString();
                }
            } catch (Exception ignored) {
                // If it doesn't have a getId method, we just use N/A
            }
        }

        String details = String.format("%s %s through %s", action, entityType, methodName);
        auditLogService.logAction(action, entityType, entityId, performedBy, details);
    }

    private String getActionFromMethodName(String methodName) {
        if (methodName.startsWith("create")) return "CREATE";
        if (methodName.startsWith("update")) return "UPDATE";
        if (methodName.startsWith("delete")) return "DELETE";
        return "UNKNOWN";
    }
}
