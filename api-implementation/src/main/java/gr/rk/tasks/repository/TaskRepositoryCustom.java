package gr.rk.tasks.repository;

import gr.rk.tasks.dto.TaskCriteriaDTO;
import gr.rk.tasks.entity.Task;
import org.springframework.data.domain.Page;

import javax.transaction.Transactional;

public interface TaskRepositoryCustom {

    @Transactional
    Task saveTask(Task task);

    Page<Task> findTasksDynamicJPQL(TaskCriteriaDTO taskCriteriaDTO);
}
