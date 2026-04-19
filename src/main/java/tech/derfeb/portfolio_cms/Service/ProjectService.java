package tech.derfeb.portfolio_cms.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.derfeb.portfolio_cms.Model.ProjectModel;
import tech.derfeb.portfolio_cms.Repository.ProjectRepository;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public ProjectModel createProject(ProjectModel project) {
        return projectRepository.save(project);
    }

    public ProjectModel updateProject(String id, ProjectModel project) {
        return projectRepository.save(project);
    }

    public void deleteProject(String id) {
        projectRepository.deleteById(id);
    }

    public ProjectModel getProjectById(String id) {
        return projectRepository.findById(id).orElse(null);
    }

    public List<ProjectModel> getAllProjects() {
        return projectRepository.findAll();
    }

    // TODO: Implement project stats
    public void getProjectStats() {
    }
}