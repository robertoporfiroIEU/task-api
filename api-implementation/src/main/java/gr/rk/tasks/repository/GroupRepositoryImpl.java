package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class GroupRepositoryImpl implements GroupRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Group> findGroupDynamicJPQL(Pageable pageable, String name, String creationDateFrom, String creationDateTo) {
        String baseJQL = "SELECT g from Group g ";
        String countJPQL = "SELECT count(g.name) from Group g";
        String whereClause = "";

        // count total results for pagination reason
        long totalResults = entityManager.createQuery(countJPQL, Long.class).getSingleResult();

        List<String> filters = getFilters(
                name,
                creationDateFrom,
                creationDateTo
        );

        if (!filters.isEmpty()) {
            whereClause = "where " + String.join(" AND ", filters);
        }

        TypedQuery<Group> groupTypedQuery = entityManager.createQuery(baseJQL + whereClause, Group.class)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize());

        // bind the parameters to avoid sql injections
        if (!filters.isEmpty()) {
            if (Objects.nonNull(name)) {
                groupTypedQuery.setParameter("name", "%" + name + "%");
            }

            if (Objects.nonNull(creationDateFrom) && Objects.nonNull(creationDateTo)) {
                groupTypedQuery.setParameter("creationDateFrom", ZonedDateTime.parse(creationDateFrom).toLocalDateTime());
                groupTypedQuery.setParameter("creationDateTo", ZonedDateTime.parse(creationDateTo).toLocalDateTime());
            } else if (Objects.nonNull(creationDateFrom)) {
                groupTypedQuery.setParameter("creationDateFrom", ZonedDateTime.parse(creationDateFrom).toLocalDateTime());
            } else if (Objects.nonNull(creationDateTo)) {
                groupTypedQuery.setParameter("creationDateTo", ZonedDateTime.parse(creationDateTo).toLocalDateTime());
            }
        }

        return new PageImpl<>(groupTypedQuery.getResultList(), pageable, totalResults);
    }

    private List<String> getFilters(
            String name,
            String creationDateFrom,
            String creationDateTo
    ) {
        List<String> filters = new ArrayList<>();

        // create where clause
        if (Objects.nonNull(name)) {
            filters.add("g.name like :name");
        }

        if (Objects.nonNull(creationDateFrom) && Objects.nonNull(creationDateTo)) {
            filters.add("g.createdAt >= :creationDateFrom AND g.createdAt <= :creationDateTo");
        } else if (Objects.nonNull(creationDateFrom)) {
            filters.add("g.createdAt >= :creationDateFrom");
        } else if (Objects.nonNull(creationDateTo)) {
            filters.add("g.createdAt <= :creationDateTo");
        }

        return filters;
    }
}
