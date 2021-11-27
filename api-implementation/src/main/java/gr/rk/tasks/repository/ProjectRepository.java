package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String>, ProjectRepositoryCustom {
    Optional<Project> findProjectByIdentifierAndApplicationUserAndDeleted(String identifier, String applicationUser, boolean deleted);
}
