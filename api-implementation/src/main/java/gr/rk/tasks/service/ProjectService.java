package gr.rk.tasks.service;

import gr.rk.tasks.dto.ProjectCriteriaDTO;
import gr.rk.tasks.entity.Project;
import gr.rk.tasks.exception.ApplicationException;
import gr.rk.tasks.exception.i18n.I18nErrorMessage;
import gr.rk.tasks.repository.ProjectRepository;
import gr.rk.tasks.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final UserPrincipal userPrincipal;

    @Value("${applicationConfigurations.projectService.pageMaxSize: 25}")
    private int maxSize;

    @Autowired
    public ProjectService(
            ProjectRepository projectRepository,
            UserService userService,
            UserPrincipal userPrincipal
    ) {
        this.projectRepository = projectRepository;
        this.userPrincipal = userPrincipal;
    }

    @Transactional
    public Project createProject(Project project) throws org.springframework.dao.DataIntegrityViolationException {
        try {
            project.setCreatedBy(userPrincipal.getPropagatedUser());
            return projectRepository.saveProject(project);
        }
        catch(org.springframework.dao.DataIntegrityViolationException e) {
            throw new ConstraintViolationException("This project already exists.", null);
        }
    }

    public Page<Project> getProjects(ProjectCriteriaDTO projectCriteriaDTO) {
        Pageable page = projectCriteriaDTO.getPageable();

        if (projectCriteriaDTO.getPageable().getPageSize() > maxSize) {
            page = PageRequest.of(projectCriteriaDTO.getPageable().getPageNumber(), maxSize, projectCriteriaDTO.getPageable().getSort());
        }

        projectCriteriaDTO.setApplicationUser(userPrincipal.getClientName());
        projectCriteriaDTO.setPageable(page);

        return projectRepository.findProjectsDynamicJPQL(projectCriteriaDTO);
    }

    public Optional<Project> getProject(String identifier) {
        return projectRepository.findProjectByIdentifierAndApplicationUserAndDeleted(
                identifier,
                userPrincipal.getClientName(),
                false
        );
    }

    @Transactional
    public void deleteProjectLogical(String projectIdentifier) {
        Optional<Project> oProject = projectRepository.
                findProjectByIdentifierAndApplicationUserAndDeleted(projectIdentifier, userPrincipal.getClientName(), false);

        if (oProject.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.PROJECT_NOT_FOUND);
        }

        Project project = oProject.get();
        project.setDeleted(true);
    }

    @Transactional
    public Project updateProject(Project projectWithUpdatedValues) {
        Optional<Project> oProject = projectRepository.
                findProjectByIdentifierAndApplicationUserAndDeleted(projectWithUpdatedValues.getIdentifier(), userPrincipal.getClientName(), false);

        if (oProject.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.PROJECT_NOT_FOUND);
        }

        Project project = oProject.get();
        project.setName(projectWithUpdatedValues.getName());
        project.setPrefixIdentifier(projectWithUpdatedValues.getPrefixIdentifier());
        project.setDescription(projectWithUpdatedValues.getDescription());
        project.setConfigurations(projectWithUpdatedValues.getConfigurations());

        return projectRepository.save(project);
    }
}
