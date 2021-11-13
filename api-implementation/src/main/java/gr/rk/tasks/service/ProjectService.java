package gr.rk.tasks.service;

import gr.rk.tasks.entity.Project;
import gr.rk.tasks.exception.i18n.I18nErrorMessage;
import gr.rk.tasks.exception.i18n.UserNotFoundException;
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
import java.util.Objects;
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
    public Project createProject(Project project) {
        if (Objects.isNull(project.getCreatedBy()) || !userRepository.existsByUsername(project.getCreatedBy().getUsername())) {
            throw new UserNotFoundException(I18nErrorMessage.USER_NOT_FOUND);
        }

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

        return projectRepository.findProjectDynamicJPQL(
                page,
                identifier,
                name,
                creationDateFrom,
                creationDateTo,
                createdBy
        );
    }

    public Optional<Project> getProject(String identifier) {
        return projectRepository.findProjectByIdentifierAndApplicationUser(
                identifier,
                userPrincipal.getApplicationUser()
        );
    }

    public void deleteProject(String projectIdentifier) {
        projectRepository.deleteByIdentifier(projectIdentifier);
    }
}
