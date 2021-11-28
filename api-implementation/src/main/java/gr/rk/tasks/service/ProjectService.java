package gr.rk.tasks.service;

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
    public Project createProject(Project project, String createdBy) {
        // validations
        Optional<User> oUser = userRepository
                .findByUsernameAndApplicationUserAndDeleted(createdBy, userPrincipal.getApplicationUser(), false);

        if (oUser.isEmpty()) {
            throw new UserNotFoundException(I18nErrorMessage.USER_NOT_FOUND);
        }

        // happy path
        project.setCreatedBy(oUser.get());
        return projectRepository.saveProject(project);
    }

    public Page<Project> getProjects(
            Pageable pageable,
            String identifier,
            String name,
            String creationDateFrom,
            String creationDateTo,
            String createdBy
    ) {
        Pageable page = pageable;

        if (pageable.getPageSize() > maxSize) {
            page = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        return projectRepository.findProjectsDynamicJPQL(
                page,
                identifier,
                name,
                creationDateFrom,
                creationDateTo,
                createdBy,
                userPrincipal.getApplicationUser()
        );
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
