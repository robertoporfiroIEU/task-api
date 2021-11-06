package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Project;
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
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public Page<Project> findProjectDynamicJPQL(
            Pageable pageable,
            String identifier,
            String name,
            String creationDateFrom,
            String creationDateTo,
            String createdBy) {

        String baseJQL = "SELECT p from Project p ";
        String countJPQL = "SELECT count(p.id) from Project p";
        String whereClause = "";

        // count total results for pagination reason
        long totalResults = entityManager.createQuery(countJPQL, Long.class).getSingleResult();

        List<String> filters = getFilters(
                identifier,
                name,
                creationDateFrom,
                creationDateTo,
                createdBy
        );

        if (!filters.isEmpty()) {
            whereClause = "where " + String.join(" AND ", filters);
        }

        TypedQuery<Project> projectTypedQuery = entityManager.createQuery(baseJQL + whereClause, Project.class)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize());

        // bind the parameters to avoid sql injections
        if (!filters.isEmpty()) {
            if (Objects.nonNull(identifier)) {
                projectTypedQuery.setParameter("identifier", "%" + identifier + "%");
            }

            if (Objects.nonNull(name)) {
                projectTypedQuery.setParameter("name", "%" + name + "%");
            }

            if (Objects.nonNull(createdBy)) {
                projectTypedQuery.setParameter("createdBy", "%" + createdBy + "%");
            }

            if (Objects.nonNull(creationDateFrom) && Objects.nonNull(creationDateTo)) {
                projectTypedQuery.setParameter("creationDateFrom", ZonedDateTime.parse(creationDateFrom).toLocalDateTime());
                projectTypedQuery.setParameter("creationDateTo", ZonedDateTime.parse(creationDateTo).toLocalDateTime());
            } else if (Objects.nonNull(creationDateFrom)) {
                projectTypedQuery.setParameter("creationDateFrom", ZonedDateTime.parse(creationDateFrom).toLocalDateTime());
            } else if (Objects.nonNull(creationDateTo)) {
                projectTypedQuery.setParameter("creationDateTo", ZonedDateTime.parse(creationDateTo).toLocalDateTime());
            }
        }

        return new PageImpl<>(projectTypedQuery.getResultList(), pageable, totalResults);

    }

    private List<String> getFilters(
            String identifier,
            String name,
            String creationDateFrom,
            String creationDateTo,
            String createdBy
    ) {
        List<String> filters = new ArrayList<>();

        // create where clause
        if (Objects.nonNull(identifier)) {
            filters.add("p.identifier like :identifier");
        }

        if (Objects.nonNull(name)) {
            filters.add("p.name like :name");
        }

        if (Objects.nonNull(createdBy)) {
            filters.add("p.createdBy like :createdBy");
        }

        if (Objects.nonNull(creationDateFrom) && Objects.nonNull(creationDateTo)) {
            filters.add("p.createdAt >= :creationDateFrom AND p.createdAt <= :creationDateTo");
        } else if (Objects.nonNull(creationDateFrom)) {
            filters.add("p.createdAt >= :creationDateFrom");
        } else if (Objects.nonNull(creationDateTo)) {
            filters.add("p.createdAt <= :creationDateTo");
        }

        return filters;
    }
}
