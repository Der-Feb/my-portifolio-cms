package tech.derfeb.portfolio_cms.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tech.derfeb.portfolio_cms.Dto.PartnerRequestDto;
import tech.derfeb.portfolio_cms.Dto.ProjectRequestDto;
import tech.derfeb.portfolio_cms.Model.ProjectModel;
import tech.derfeb.portfolio_cms.Service.ProjectService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // ── Project endpoints ─────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<ProjectModel> createProject(@RequestBody ProjectRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProjectModel>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectModel> getProjectById(@PathVariable String id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectModel> updateProject(
            @PathVariable String id,
            @RequestBody ProjectRequestDto dto) {
        return ResponseEntity.ok(projectService.updateProject(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    // ── Partner endpoints ─────────────────────────────────────────────────────

    @PostMapping("/{projectId}/partners")
    public ResponseEntity<ProjectModel> addPartner(
            @PathVariable String projectId,
            @RequestBody PartnerRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.addPartner(projectId, dto));
    }

    @PutMapping("/{projectId}/partners/{partnerId}")
    public ResponseEntity<ProjectModel> updatePartner(
            @PathVariable String projectId,
            @PathVariable String partnerId,
            @RequestBody PartnerRequestDto dto) {
        return ResponseEntity.ok(projectService.updatePartner(projectId, partnerId, dto));
    }

    @DeleteMapping("/{projectId}/partners/{partnerId}")
    public ResponseEntity<Void> deletePartner(
            @PathVariable String projectId,
            @PathVariable String partnerId) {
        projectService.deletePartner(projectId, partnerId);
        return ResponseEntity.noContent().build();
    }
}
