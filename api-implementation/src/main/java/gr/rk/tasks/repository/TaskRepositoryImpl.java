package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class TaskRepositoryImpl implements TasksRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public Page<Task> findTasksDynamicJPQL(
            Pageable pageable,
            String identifier,
            String name,
            String status,
            String creationDate,
            String createdBy,
            String dueDate) {

        String baseJQL = "SELECT t from Task t ";
        String countJPQL = "SELECT count(t.id) from Task t";
        String whereClause = "";

        // count total results for pagination reason
        long totalResults = entityManager.createQuery(countJPQL, Long.class).getSingleResult();

        List<String> filters = getFilters(
                identifier,
                name,
                status,
                creationDate,
                createdBy,
                dueDate
        );

        if (!filters.isEmpty()) {
            whereClause = "where " + String.join(" AND ", filters);
        }

        TypedQuery<Task> taskTypedQuery = entityManager.createQuery(baseJQL + whereClause, Task.class)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize());

        // bind the parameters to avoid sql injections
        if (!filters.isEmpty()) {
            if (Objects.nonNull(identifier)) {
                taskTypedQuery.setParameter("identifier", "%" + identifier + "%");
            }

            if (Objects.nonNull(name)) {
                taskTypedQuery.setParameter("name", "%" + name + "%");
            }

            if (Objects.nonNull(status)) {
                taskTypedQuery.setParameter("status", "%" + status + "%");
            }

            if (Objects.nonNull(creationDate)) {
                taskTypedQuery.setParameter("createdAt", ZonedDateTime.parse(creationDate).toLocalDateTime());
            }

            if (Objects.nonNull(createdBy)) {
                taskTypedQuery.setParameter("createdBy", "%" + createdBy + "%");
            }

            if (Objects.nonNull(dueDate)) {
                taskTypedQuery.setParameter("dueDate", ZonedDateTime.parse(dueDate).toLocalDateTime());
            }
        }

        return new PageImpl<>(taskTypedQuery.getResultList(), pageable, totalResults);
    }

    private List<String> getFilters(
            String identifier,
            String name,
            String status,
            String creationDate,
            String createdBy,
            String dueDate) {
        List<String> filters = new ArrayList<>();

        // create where clause
        if (Objects.nonNull(identifier)) {
            filters.add("t.identifier like :identifier");
        }

        if (Objects.nonNull(name)) {
            filters.add("t.name like :name");
        }

        if (Objects.nonNull(status)) {
            filters.add("t.status like :status");
        }

        if (Objects.nonNull(creationDate)) {
            filters.add("t.createdAt = :createdAt");
        }

        if (Objects.nonNull(createdBy)) {
            filters.add("t.createdBy like :createdBy");
        }
        if (Objects.nonNull(dueDate)) {
            filters.add("t.dueDate = :dueDate");
        }

        return filters;
    }
}
