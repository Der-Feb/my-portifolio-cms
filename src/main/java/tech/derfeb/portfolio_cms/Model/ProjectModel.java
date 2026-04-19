package tech.derfeb.portfolio_cms.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "Project name is required")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String reviewImage;

    private String githubLink;

    private String liveLink;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_id")
    private List<PartnerModel> partners = new ArrayList<>();
}

@Entity
@Table(name = "partners")
@Data
@NoArgsConstructor
@AllArgsConstructor
class PartnerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String imagePreview;

    @ElementCollection
    @CollectionTable(name = "partner_links", joinColumns = @JoinColumn(name = "project_id"))
    private List<String> links = new ArrayList<>();
}
