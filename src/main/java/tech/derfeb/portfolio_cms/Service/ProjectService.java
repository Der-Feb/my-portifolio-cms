package tech.derfeb.portfolio_cms.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import tech.derfeb.portfolio_cms.Dto.PartnerRequestDto;
import tech.derfeb.portfolio_cms.Dto.ProjectRequestDto;
import tech.derfeb.portfolio_cms.Model.PartnerModel;
import tech.derfeb.portfolio_cms.Model.ProjectModel;
import tech.derfeb.portfolio_cms.Repository.ProjectRepository;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    // ── Project CRUD ──────────────────────────────────────────────────────────

    public ProjectModel createProject(ProjectRequestDto dto) {
        ProjectModel project = new ProjectModel();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setReviewImage(dto.getReviewImage());
        project.setGithubLink(dto.getGithubLink());
        project.setLiveLink(dto.getLiveLink());
        project.setTechStack(dto.getTechStack());

        if (dto.getPartners() != null && !dto.getPartners().isEmpty()) {
            List<PartnerModel> partners = new ArrayList<>();
            for (PartnerRequestDto p : dto.getPartners()) {
                PartnerModel partner = new PartnerModel();
                partner.setName(p.getName());
                partner.setImagePreview(p.getImagePreview());
                if (p.getLinks() != null) {
                    partner.setLinks(p.getLinks());
                }
                partners.add(partner);
            }
            project.setPartners(partners);
        }

        return projectRepository.save(project);
    }

    public List<ProjectModel> getAllProjects() {
        return projectRepository.findAll();
    }

    public ProjectModel getProjectById(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    public ProjectModel updateProject(String id, ProjectRequestDto dto) {
        ProjectModel project = getProjectById(id);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            project.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            project.setDescription(dto.getDescription());
        }
        if (dto.getReviewImage() != null && !dto.getReviewImage().isBlank()) {
            project.setReviewImage(dto.getReviewImage());
        }
        if (dto.getGithubLink() != null && !dto.getGithubLink().isBlank()) {
            project.setGithubLink(dto.getGithubLink());
        }
        if (dto.getLiveLink() != null && !dto.getLiveLink().isBlank()) {
            project.setLiveLink(dto.getLiveLink());
        }
        if (dto.getTechStack() != null) {
            project.setTechStack(dto.getTechStack());
        }

        return projectRepository.save(project);
    }

    public void deleteProject(String id) {
        if (!projectRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
        projectRepository.deleteById(id);
    }

    // ── Partner CRUD ──────────────────────────────────────────────────────────

    public ProjectModel addPartner(String projectId, PartnerRequestDto dto) {
        ProjectModel project = getProjectById(projectId);

        PartnerModel partner = new PartnerModel();
        partner.setName(dto.getName());
        partner.setImagePreview(dto.getImagePreview());
        if (dto.getLinks() != null) {
            partner.setLinks(dto.getLinks());
        }

        project.getPartners().add(partner);
        return projectRepository.save(project);
    }

    public ProjectModel updatePartner(String projectId, String partnerId, PartnerRequestDto dto) {
        ProjectModel project = getProjectById(projectId);

        PartnerModel partner = project.getPartners().stream()
                .filter(p -> p.getId().equals(partnerId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partner not found"));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            partner.setName(dto.getName());
        }
        if (dto.getImagePreview() != null && !dto.getImagePreview().isBlank()) {
            partner.setImagePreview(dto.getImagePreview());
        }
        if (dto.getLinks() != null && !dto.getLinks().isEmpty()) {
            partner.setLinks(dto.getLinks());
        }

        return projectRepository.save(project);
    }

    public void deletePartner(String projectId, String partnerId) {
        ProjectModel project = getProjectById(projectId);

        boolean removed = project.getPartners().removeIf(p -> p.getId().equals(partnerId));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Partner not found");
        }

        projectRepository.save(project);
    }
}
