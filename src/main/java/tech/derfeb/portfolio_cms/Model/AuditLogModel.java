package tech.derfeb.portfolio_cms.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String action; // CREATE, UPDATE, DELETE
    private String entityType; // Project, Testimonial, etc.
    private String entityId;
    private String performedBy; // username

    @Column(columnDefinition = "TEXT")
    private String details;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;
}
