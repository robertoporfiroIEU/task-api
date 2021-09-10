package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TasksRepositoryCustom {
    Page<Task> findTasksDynamicJPQL(
            Pageable pageable,
            String identifier,
            String name,
            String status,
            String creationDate,
            String createdBy,
            String dueDate);
}
