package tech.derfeb.portfolio_cms.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "testimonials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String role;
    private String company;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private String projectWorkedOn;
    private String profileImage;

    @ElementCollection
    @CollectionTable(name = "testimonial_contact_links", joinColumns = @JoinColumn(name = "testimonial_id"))
    private List<ContactLink> contactLinks = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactLink {
        private String label;
        private String url;
    }
}
