package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findTaskByIdentifierAndApplicationUser(String identifier, String applicationUser);

    void deleteByIdentifier(String identifier);
}
