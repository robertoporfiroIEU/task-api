package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {

    Optional<Project> findProjectByIdAndPrefixIdentifierAndApplicationUser(Long id, String prefixIdentifier, String applicationUser);

    void deleteByIdAndPrefixIdentifierAndApplicationUser(Long id, String prefixIdentifier, String applicationUser);
}
