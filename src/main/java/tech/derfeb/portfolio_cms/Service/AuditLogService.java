package tech.derfeb.portfolio_cms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.derfeb.portfolio_cms.Model.AuditLogModel;
import tech.derfeb.portfolio_cms.Repository.AuditLogRepository;

import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logAction(String action, String entityType, String entityId, String performedBy, String details) {
        AuditLogModel log = new AuditLogModel();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setPerformedBy(performedBy);
        log.setDetails(details);
        auditLogRepository.save(log);
    }

    public List<AuditLogModel> getAllLogs() {
        return auditLogRepository.findAll();
    }
}
