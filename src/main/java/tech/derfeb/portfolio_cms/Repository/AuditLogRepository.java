package tech.derfeb.portfolio_cms.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.derfeb.portfolio_cms.Model.AuditLogModel;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogModel, String> {
}
