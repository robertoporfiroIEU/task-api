package gr.rk.tasks.repository;

import gr.rk.tasks.dto.ProjectCriteriaDTO;
import gr.rk.tasks.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;

public interface ProjectRepositoryCustom {

    @Transactional
    Project saveProject(Project project);

    Page<Project> findProjectsDynamicJPQL(ProjectCriteriaDTO projectCriteriaDTO);
}
