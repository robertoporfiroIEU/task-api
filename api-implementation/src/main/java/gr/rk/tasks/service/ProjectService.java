package gr.rk.tasks.service;

import gr.rk.tasks.dto.ProjectCriteriaDTO;
import gr.rk.tasks.entity.Project;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.exception.i18n.I18nErrorMessage;
import gr.rk.tasks.exception.ProjectNotFoundException;
import gr.rk.tasks.exception.UserNotFoundException;
import gr.rk.tasks.repository.ProjectRepository;
import gr.rk.tasks.repository.UserRepository;
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

    private final UserRepository userRepository;

    private final UserPrincipal userPrincipal;

    @Value("${applicationConfigurations.projectService.pageMaxSize: 25}")
    private int maxSize;

    @Autowired
    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            UserPrincipal userPrincipal
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.userPrincipal = userPrincipal;
    }

    @Transactional
    public Project createProject(Project project, String createdBy) throws org.springframework.dao.DataIntegrityViolationException {
        // validations
        Optional<User> oUser = userRepository
                .findByUsernameAndApplicationUserAndDeleted(createdBy, userPrincipal.getApplicationUser(), false);

        if (oUser.isEmpty()) {
            throw new UserNotFoundException(I18nErrorMessage.USER_NOT_FOUND);
        }

        // happy path
        project.setCreatedBy(oUser.get());
        try {
            return projectRepository.saveProject(project);
        }
        catch(org.springframework.dao.DataIntegrityViolationException e) {
            throw new ConstraintViolationException("Prefix identification already exist", null);
        }
    }

    public Page<Project> getProjects(ProjectCriteriaDTO projectCriteriaDTO) {
        Pageable page = projectCriteriaDTO.getPageable();

        if (projectCriteriaDTO.getPageable().getPageSize() > maxSize) {
            page = PageRequest.of(projectCriteriaDTO.getPageable().getPageNumber(), maxSize, projectCriteriaDTO.getPageable().getSort());
        }

        projectCriteriaDTO.setApplicationUser(userPrincipal.getApplicationUser());
        projectCriteriaDTO.setPageable(page);

        return projectRepository.findProjectsDynamicJPQL(projectCriteriaDTO);
    }

    public Optional<Project> getProject(String identifier) {
        return projectRepository.findProjectByIdentifierAndApplicationUserAndDeleted(
                identifier,
                userPrincipal.getApplicationUser(),
                false
        );
    }

    @Transactional
    public void deleteProjectLogical(String projectIdentifier) {
        Optional<Project> oProject = projectRepository.
                findProjectByIdentifierAndApplicationUserAndDeleted(projectIdentifier, userPrincipal.getApplicationUser(), false);

        if (oProject.isEmpty()) {
            throw new ProjectNotFoundException(I18nErrorMessage.PROJECT_NOT_FOUND);
        }

        Project project = oProject.get();
        project.setDeleted(true);
    }
}
