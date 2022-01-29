package gr.rk.tasks.repository;

import gr.rk.tasks.dto.RepositoryResultDTO;
import gr.rk.tasks.dto.TaskCriteriaDTO;
import gr.rk.tasks.entity.Task;
import gr.rk.tasks.util.Util;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Repository
public class TaskRepositoryImpl implements TaskRepositoryCustom {

    private static final Map<String, String> APPLICABLE_SORT_FIELDS_PATH_VARIABLE_MAP = Map.of(
            "identifier", "identifier",
            "name", "name",
            "status", "status",
            "createdAt", "createdAt",
            "dueDate", "dueDate",
            "createdBy", "createdBy.username"
    );

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Task saveTask(Task task) {
        String lastTaskIdSQL = "SELECT t.id from Task t order by t.id desc";
        // Get the last id of the task entity and increment by one
        long lastId;

        try {
            lastId = entityManager.createQuery(lastTaskIdSQL, Long.class).setMaxResults(1).getSingleResult();
            lastId++;
        } catch (NoResultException e) {
            lastId = 0;
        }

        task.setIdentifier(task.getProject().getPrefixIdentifier() + "-" + lastId);
        entityManager.persist(task);
        return task;
    }

    @Override
    public Page<Task> findTasksDynamicJPQL(TaskCriteriaDTO taskCriteriaDTO) {
        List<Consumer<TypedQuery<Task>>> tasksTypedQueryParamBinders = new ArrayList<>();
        List<Consumer<TypedQuery<Long>>> totalResultsQueryParamBinders = new ArrayList<>();

        RepositoryResultDTO<Task> repositoryResultDTO = findTasks(taskCriteriaDTO, tasksTypedQueryParamBinders, totalResultsQueryParamBinders);

        TypedQuery<Task> tasksTypedQuery = repositoryResultDTO.getResultTypedQuery();
        TypedQuery<Long> totalResultsQuery = repositoryResultDTO.getTotalResultTypedQuery();

        tasksTypedQueryParamBinders.forEach(typedQueryConsumer -> typedQueryConsumer.accept(tasksTypedQuery));
        totalResultsQueryParamBinders.forEach(typedQueryConsumer -> typedQueryConsumer.accept(totalResultsQuery));

        return new PageImpl<>(tasksTypedQuery.getResultList(), taskCriteriaDTO.getPageable(), totalResultsQuery.getSingleResult());
    }

    private RepositoryResultDTO<Task> findTasks(
            TaskCriteriaDTO taskCriteriaDTO,
            List<Consumer<TypedQuery<Task>>> tasksTypedQueryParamBinders,
            List<Consumer<TypedQuery<Long>>> totalResultsQueryParamBinders
    ) {
        List<String> filters = new ArrayList<>();
        String entityVariable = "t";
        String entityVariableWithDot = entityVariable + ".";
        String joinPart = "";

        if (Objects.nonNull(taskCriteriaDTO.getAssignedTo())) {
            joinPart = " join " + entityVariableWithDot + "assigns a ";
            filters.add("(a.user.username = :assignedTo OR a.group.name = :assignedTo)");
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter("assignedTo",   taskCriteriaDTO.getAssignedTo())));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter("assignedTo", taskCriteriaDTO.getAssignedTo())));
        }

        if (Objects.nonNull(taskCriteriaDTO.getSpectator())) {
            joinPart = " join " + entityVariableWithDot + "spectators s ";
            filters.add("s.user.username = :spectator OR s.group.name = :spectator");
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter("spectator",   taskCriteriaDTO.getSpectator())));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter("spectator", taskCriteriaDTO.getSpectator())));
        }

        if (Objects.nonNull(taskCriteriaDTO.getIdentifier())) {
            filters.add(entityVariableWithDot + "identifier = :identifier");
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter("identifier", taskCriteriaDTO.getIdentifier())));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter("identifier", taskCriteriaDTO.getIdentifier())));
        }

        if (Objects.nonNull(taskCriteriaDTO.getProjectIdentifier())) {
            filters.add(entityVariableWithDot + "project.identifier = :projectIdentifier");
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter("projectIdentifier", taskCriteriaDTO.getProjectIdentifier())));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter("projectIdentifier", taskCriteriaDTO.getProjectIdentifier())));
        }

        if (Objects.nonNull(taskCriteriaDTO.getName())) {
            filters.add(entityVariableWithDot + "name like :name");
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter("name", "%" + taskCriteriaDTO.getName() + "%")));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter("name", "%" + taskCriteriaDTO.getName() + "%")));
        }

        if (Objects.nonNull(taskCriteriaDTO.getStatus())) {
            filters.add(entityVariableWithDot + "status like :status");
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter("status", "%" + taskCriteriaDTO.getStatus() + "%")));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter("status", "%" + taskCriteriaDTO.getStatus() + "%")));
        }

        if (Objects.nonNull(taskCriteriaDTO.getCreatedBy())) {
            filters.add(entityVariableWithDot + "createdBy.username like :createdBy");
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter("createdBy", "%" + taskCriteriaDTO.getCreatedBy() + "%")));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter("createdBy", "%" + taskCriteriaDTO.getCreatedBy() + "%")));
        }

        if (Objects.nonNull(taskCriteriaDTO.getCreationDateFrom()) && Objects.nonNull(taskCriteriaDTO.getCreationDateTo())) {
            filters.add(entityVariableWithDot + "createdAt >= :creationDateFrom AND " + entityVariableWithDot + "createdAt <= :creationDateTo");
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "creationDateFrom", ZonedDateTime.parse(taskCriteriaDTO.getCreationDateFrom()).toLocalDateTime())
            ));
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "creationDateTo", ZonedDateTime.parse(taskCriteriaDTO.getCreationDateTo()).toLocalDateTime())
            ));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "creationDateFrom", ZonedDateTime.parse(taskCriteriaDTO.getCreationDateFrom()).toLocalDateTime())
            ));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "creationDateTo", ZonedDateTime.parse(taskCriteriaDTO.getCreationDateTo()).toLocalDateTime())
            ));
        } else if (Objects.nonNull(taskCriteriaDTO.getCreationDateFrom())) {
            filters.add(entityVariableWithDot + "createdAt >= :creationDateFrom");
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "creationDateFrom", ZonedDateTime.parse(taskCriteriaDTO.getCreationDateFrom()).toLocalDateTime())
            ));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "creationDateFrom", ZonedDateTime.parse(taskCriteriaDTO.getCreationDateFrom()).toLocalDateTime())
            ));
        } else if (Objects.nonNull(taskCriteriaDTO.getCreationDateTo())) {
            filters.add(entityVariableWithDot + "createdAt <= :creationDateTo");
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "creationDateTo", ZonedDateTime.parse(taskCriteriaDTO.getCreationDateTo()).toLocalDateTime())
            ));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "creationDateTo", ZonedDateTime.parse(taskCriteriaDTO.getCreationDateTo()).toLocalDateTime())
            ));
        }

        if (Objects.nonNull(taskCriteriaDTO.getDueDateFrom()) && Objects.nonNull(taskCriteriaDTO.getDueDateTo())) {
            filters.add(entityVariableWithDot + "dueDate >= :creationDateFrom AND " + entityVariableWithDot + "createdAt <= :creationDateTo");
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "dueDateFrom", ZonedDateTime.parse(taskCriteriaDTO.getDueDateFrom()).toLocalDateTime())
            ));
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "dueDateTo", ZonedDateTime.parse(taskCriteriaDTO.getDueDateFrom()).toLocalDateTime())
            ));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "dueDateFrom", ZonedDateTime.parse(taskCriteriaDTO.getDueDateFrom()).toLocalDateTime())
            ));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "dueDateTo", ZonedDateTime.parse(taskCriteriaDTO.getDueDateFrom()).toLocalDateTime())
            ));
        } else if (Objects.nonNull(taskCriteriaDTO.getDueDateFrom())) {
            filters.add(entityVariableWithDot + "dueDate >= :dueDateFrom");
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "dueDateFrom", ZonedDateTime.parse(taskCriteriaDTO.getDueDateFrom()).toLocalDateTime())
            ));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "dueDateFrom", ZonedDateTime.parse(taskCriteriaDTO.getDueDateFrom()).toLocalDateTime())
            ));
        } else if (Objects.nonNull(taskCriteriaDTO.getDueDateTo())) {
            filters.add(entityVariableWithDot + "dueDate <= :dueDateTo");
            tasksTypedQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "dueDateTo", ZonedDateTime.parse(taskCriteriaDTO.getDueDateFrom()).toLocalDateTime())
            ));
            totalResultsQueryParamBinders.add((taskTypedQuery -> taskTypedQuery.setParameter(
                    "dueDateTo", ZonedDateTime.parse(taskCriteriaDTO.getDueDateFrom()).toLocalDateTime())
            ));
        }

        String fromPart = "FROM Task " + entityVariable + " " + joinPart;
        String wherePart = "WHERE " + entityVariableWithDot + "deleted = false AND " + entityVariableWithDot + "applicationUser = '" + taskCriteriaDTO.getApplicationUser() + "' ";

        if (!filters.isEmpty()) {
            wherePart += "AND " + String.join(" AND ", filters);
        }

        String orderByPart = Util.getOrderByStatement(entityVariable, taskCriteriaDTO.getPageable().getSort(), APPLICABLE_SORT_FIELDS_PATH_VARIABLE_MAP);

        if (orderByPart.equals("")) {
            orderByPart = " ORDER BY " + entityVariableWithDot +"createdAt DESC";
        }

        // count total results for pagination reason
        TypedQuery<Long> totalResultsTypedQuery = entityManager.createQuery("SELECT COUNT(" + entityVariable + ") " + fromPart + wherePart, Long.class);
        TypedQuery<Task> taskTypedQuery = entityManager.createQuery("SELECT " + entityVariable + " " + fromPart + wherePart + orderByPart, Task.class)
                .setMaxResults(taskCriteriaDTO.getPageable().getPageSize())
                .setFirstResult(taskCriteriaDTO.getPageable().getPageNumber() * taskCriteriaDTO.getPageable().getPageSize());

        return new RepositoryResultDTO<>(totalResultsTypedQuery, taskTypedQuery);
    }
}
