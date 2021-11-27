package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;

public interface ProjectRepositoryCustom {

    @Transactional
    Project saveProject(Project project);

    Page<Project> findProjectsDynamicJPQL(
            Pageable pageable,
            String identifier,
            String name,
            String creationDateFrom,
            String creationDateTo,
            String createdBy,
            String applicationUser
            );
}
