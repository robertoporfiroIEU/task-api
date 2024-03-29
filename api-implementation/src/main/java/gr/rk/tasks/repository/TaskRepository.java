package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom {

    Optional<Task> findTaskByIdentifierAndApplicationUserAndDeleted(String identifier, String applicationUser, boolean deleted);

    void deleteByIdentifier(String identifier);

}
