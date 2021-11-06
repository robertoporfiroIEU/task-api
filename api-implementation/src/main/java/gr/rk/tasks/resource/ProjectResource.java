package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.ProjectsApi;
import gr.rk.tasks.V1.dto.ProjectDTO;
import gr.rk.tasks.entity.Project;
import gr.rk.tasks.mapper.ProjectMapper;
import gr.rk.tasks.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Validated
public class ProjectResource implements ProjectsApi {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    @Autowired
    public ProjectResource(ProjectService projectService, ProjectMapper projectMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
    }

    @Override
    public ResponseEntity<Page<ProjectDTO>> getProjects(
            Pageable pageable,
            String identifier,
            String name,
            String creationDateFrom,
            String creationDateTo,
            String createdBy) {

        List<ProjectDTO> projectsDTO = new ArrayList<>();
        Page<ProjectDTO> projectsDTOPage = new PageImpl<>(projectsDTO);

        Page<Project> projectsEntity = projectService.getProjects(
                pageable,
                identifier,
                name,
                creationDateFrom,
                creationDateTo,
                createdBy
        );

        if (!projectsEntity.isEmpty()) {
            projectsDTOPage = projectMapper.toPageProjectsDTO(projectsEntity);
        }

        return ResponseEntity.ok(projectsDTOPage);
    }

    @Override
    public ResponseEntity<ProjectDTO> getProject(UUID identifier) {
        ProjectDTO projectDTO = new ProjectDTO();
        Optional<Project> oProjectEntity = projectService.getProject(identifier.toString());

        if (oProjectEntity.isPresent()) {
            projectDTO = this.projectMapper.toProjectDTO(oProjectEntity.get());
        }

        return ResponseEntity.ok(projectDTO);
    }

    @Override
    public ResponseEntity<ProjectDTO> createProject(ProjectDTO projectDTO) {
        Project projectEntity = projectMapper.toProject(projectDTO);
        projectEntity = projectService.createProject(projectEntity);

        ProjectDTO projectDTOResponse = projectMapper.toProjectDTO(projectEntity);
        return  ResponseEntity.ok(projectDTOResponse);
    }

}
