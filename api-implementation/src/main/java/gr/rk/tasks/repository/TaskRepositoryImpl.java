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
public class TaskRepositoryImpl implements TaskRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public Page<Task> findTaskDynamicJPQL(
            Pageable pageable,
            String identifier,
            String name,
            String status,
            String creationDateFrom,
            String creationDateTo,
            String createdBy,
            String dueDateFrom,
            String dueDateTo) {

        String baseJQL = "SELECT t from Task t ";
        String countJPQL = "SELECT count(t.id) from Task t";
        String whereClause = "";

        // count total results for pagination reason
        long totalResults = entityManager.createQuery(countJPQL, Long.class).getSingleResult();

        List<String> filters = getFilters(
                identifier,
                name,
                status,
                creationDateFrom,
                creationDateTo,
                createdBy,
                dueDateFrom,
                dueDateTo
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

            if (Objects.nonNull(createdBy)) {
                taskTypedQuery.setParameter("createdBy", "%" + createdBy + "%");
            }

            if (Objects.nonNull(creationDateFrom) && Objects.nonNull(creationDateTo)) {
                taskTypedQuery.setParameter("creationDateFrom", ZonedDateTime.parse(creationDateFrom).toLocalDateTime());
                taskTypedQuery.setParameter("creationDateTo", ZonedDateTime.parse(creationDateTo).toLocalDateTime());
            } else if (Objects.nonNull(creationDateFrom)) {
                taskTypedQuery.setParameter("creationDateFrom", ZonedDateTime.parse(creationDateFrom).toLocalDateTime());
            } else if (Objects.nonNull(creationDateTo)) {
                taskTypedQuery.setParameter("creationDateTo", ZonedDateTime.parse(creationDateTo).toLocalDateTime());
            }

            if (Objects.nonNull(dueDateFrom) && Objects.nonNull(dueDateTo)) {
                taskTypedQuery.setParameter("dueDateFrom", ZonedDateTime.parse(dueDateFrom).toLocalDateTime());
                taskTypedQuery.setParameter("dueDateTo", ZonedDateTime.parse(dueDateTo).toLocalDateTime());
            } else if (Objects.nonNull(dueDateFrom)) {
                taskTypedQuery.setParameter("dueDateFrom", ZonedDateTime.parse(dueDateFrom).toLocalDateTime());
            } else if (Objects.nonNull(dueDateTo)) {
                taskTypedQuery.setParameter("dueDateTo", ZonedDateTime.parse(dueDateTo).toLocalDateTime());
            }
        }

        return new PageImpl<>(taskTypedQuery.getResultList(), pageable, totalResults);
    }

    private List<String> getFilters(
            String identifier,
            String name,
            String status,
            String creationDateFrom,
            String creationDateTo,
            String createdBy,
            String dueDateFrom,
            String dueDateTo) {
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

        if (Objects.nonNull(createdBy)) {
            filters.add("t.createdBy like :createdBy");
        }

        if (Objects.nonNull(creationDateFrom) && Objects.nonNull(creationDateTo)) {
            filters.add("t.createdAt >= :creationDateFrom AND t.createdAt <= :creationDateTo");
        } else if (Objects.nonNull(creationDateFrom)) {
            filters.add("t.createdAt >= :creationDateFrom");
        } else if (Objects.nonNull(creationDateTo)) {
            filters.add("t.createdAt <= :creationDateTo");
        }


        if (Objects.nonNull(dueDateFrom) && Objects.nonNull(dueDateTo)) {
            filters.add("t.dueDate >= :creationDateFrom AND t.createdAt <= :creationDateTo");
        } else if (Objects.nonNull(dueDateFrom)) {
            filters.add("t.dueDate >= :dueDateFrom");
        } else if (Objects.nonNull(dueDateTo)) {
            filters.add("t.dueDate <= :dueDateTo");
        }

        return filters;
    }
}
