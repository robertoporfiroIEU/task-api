package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;

public interface TaskRepositoryCustom {

    @Transactional
    Task saveTask(Task task);

    Page<Task> findTasksDynamicJPQL(
            Pageable pageable,
            String identifier,
            String name,
            String status,
            String creationDateFrom,
            String creationDateTo,
            String createdBy,
            String dueDateFrom,
            String dueDateTo,
            String applicationUser
            );
}
