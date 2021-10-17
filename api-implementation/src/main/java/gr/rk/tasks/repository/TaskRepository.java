package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom {

    @Query("SELECT  t FROM Task t WHERE t.id = :id AND t.project.prefixIdentifier = :prefixIdentifier AND t.applicationUser = :applicationUser ")
    Optional<Task> findTaskByByIdAndPrefixIdentifierAndApplicationUser(Long id, String prefixIdentifier, String applicationUser);

}
