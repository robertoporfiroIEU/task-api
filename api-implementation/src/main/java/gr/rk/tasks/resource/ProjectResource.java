package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.ProjectsApi;
import gr.rk.tasks.V1.dto.PaginatedProjectsDTO;
import gr.rk.tasks.V1.dto.ProjectDTO;
import gr.rk.tasks.dto.ProjectCriteriaDTO;
import gr.rk.tasks.entity.Project;
import gr.rk.tasks.mapper.ProjectMapper;
import gr.rk.tasks.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Optional;

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
    @Secured("ROLE_READ_PROJECT")
    public ResponseEntity<PaginatedProjectsDTO> getProjects(
            Pageable pageable,
            String identifier,
            String name,
            String creationDateFrom,
            String creationDateTo,
            String createdBy
    ) {
        ProjectCriteriaDTO projectCriteriaDTO = new ProjectCriteriaDTO();
        projectCriteriaDTO.setPageable(pageable);
        projectCriteriaDTO.setIdentifier(identifier);
        projectCriteriaDTO.setName(name);
        projectCriteriaDTO.setCreationDateFrom(creationDateFrom);
        projectCriteriaDTO.setCreationDateTo(creationDateTo);
        projectCriteriaDTO.setCreatedBy(createdBy);

        Page<Project> projectsEntity = projectService.getProjects(projectCriteriaDTO);

        PaginatedProjectsDTO paginatedProjectsDTO = new PaginatedProjectsDTO()
                .totalElements(0)
                .content(new ArrayList<>());

        if (!projectsEntity.isEmpty()) {
            paginatedProjectsDTO = projectMapper.toPaginatedProjectsDTO(projectsEntity);
        }

        return ResponseEntity.ok(paginatedProjectsDTO);
    }

    @Override
    @Secured("ROLE_UPDATE_PROJECT")
    public ResponseEntity<ProjectDTO> updateProject(ProjectDTO projectDTO) {
        Project project = this.projectMapper.toProject(projectDTO);
        Project projectEntity = projectService.updateProject(project);
        projectDTO = this.projectMapper.toProjectDTO(projectEntity);
        return ResponseEntity.ok(projectDTO);
    }

    @Override
    @Secured("ROLE_READ_PROJECT")
    public ResponseEntity<ProjectDTO> getProject(String identifier) {
        ProjectDTO projectDTO = new ProjectDTO();
        Optional<Project> oProjectEntity = projectService.getProject(identifier);

        if (oProjectEntity.isPresent()) {
            projectDTO = this.projectMapper.toProjectDTO(oProjectEntity.get());
        }

        return ResponseEntity.ok(projectDTO);
    }

    @Override
    @Secured("ROLE_CREATE_PROJECT")
    public ResponseEntity<ProjectDTO> createProject(ProjectDTO projectDTO) {
        Project projectEntity = projectMapper.toProject(projectDTO);
        projectEntity = projectService.createProject(projectEntity);

        ProjectDTO projectDTOResponse = projectMapper.toProjectDTO(projectEntity);
        return  ResponseEntity.ok(projectDTOResponse);
    }

    @Override
    @Secured("ROLE_DELETE_PROJECT")
    public ResponseEntity<Void> deleteProject(String identifier) {
        projectService.deleteProjectLogical(identifier);
        return ResponseEntity.ok().build();
    }


}
