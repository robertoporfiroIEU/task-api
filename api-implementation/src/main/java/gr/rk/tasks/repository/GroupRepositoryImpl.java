package gr.rk.tasks.repository;

import gr.rk.tasks.dto.GroupCriteriaDTO;
import gr.rk.tasks.dto.RepositoryResultDTO;
import gr.rk.tasks.entity.Group;
import gr.rk.tasks.util.Util;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Repository
public class GroupRepositoryImpl implements GroupRepositoryCustom {

    private static final Map<String, String> APPLICABLE_SORT_FIELDS_PATH_VARIABLE_MAP = Map.of(
            "name", "name",
            "createdAt", "createdAt"
    );

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Group> findGroupDynamicJPQL(GroupCriteriaDTO groupCriteriaDTO) {
        List<Consumer<TypedQuery<Group>>> groupsTypedQueryParamBinders = new ArrayList<>();
        List<Consumer<TypedQuery<Long>>> totalResultsQueryParamBinders = new ArrayList<>();

        RepositoryResultDTO<Group> repositoryResultDTO = findGroups(groupCriteriaDTO, groupsTypedQueryParamBinders, totalResultsQueryParamBinders);

        TypedQuery<Group> groupsTypedQuery = repositoryResultDTO.getResultTypedQuery();
        TypedQuery<Long> totalResultsQuery = repositoryResultDTO.getTotalResultTypedQuery();

        groupsTypedQueryParamBinders.forEach(typedQueryConsumer -> typedQueryConsumer.accept(groupsTypedQuery));
        totalResultsQueryParamBinders.forEach(typedQueryConsumer -> typedQueryConsumer.accept(totalResultsQuery));

        return new PageImpl<>(groupsTypedQuery.getResultList(), groupCriteriaDTO.getPageable(), totalResultsQuery.getSingleResult());
    }

    private RepositoryResultDTO<Group> findGroups(
            GroupCriteriaDTO groupCriteriaDTO,
            List<Consumer<TypedQuery<Group>>> groupsTypedQueryParamBinders,
            List<Consumer<TypedQuery<Long>>> totalResultsQueryParamBinders
    ) {
        List<String> filters = new ArrayList<>();
        String entityVariable = "g";
        String entityVariableWithDot = entityVariable + ".";

        // create where clause
        if (Objects.nonNull(groupCriteriaDTO.getName())) {
            filters.add(entityVariableWithDot + "name like :name");
            groupsTypedQueryParamBinders.add( t ->  t.setParameter("name", "%" + groupCriteriaDTO.getName() + "%"));
            totalResultsQueryParamBinders.add( t ->  t.setParameter("name", "%" + groupCriteriaDTO.getName() + "%"));
        }

        if (Objects.nonNull(groupCriteriaDTO.getCreationDateFrom()) && Objects.nonNull(groupCriteriaDTO.getCreationDateTo())) {
            filters.add(entityVariableWithDot + "createdAt >= :creationDateFrom AND " + entityVariableWithDot + "createdAt <= :creationDateTo");
            groupsTypedQueryParamBinders.add( t ->  t.setParameter("creationDateFrom", ZonedDateTime.parse(groupCriteriaDTO.getCreationDateFrom()).toLocalDateTime()));
            totalResultsQueryParamBinders.add( t ->  t.setParameter("creationDateFrom", ZonedDateTime.parse(groupCriteriaDTO.getCreationDateFrom()).toLocalDateTime()));
            groupsTypedQueryParamBinders.add( t ->  t.setParameter("creationDateTo", ZonedDateTime.parse(groupCriteriaDTO.getCreationDateTo()).toLocalDateTime()));
            totalResultsQueryParamBinders.add( t ->  t.setParameter("creationDateTo", ZonedDateTime.parse(groupCriteriaDTO.getCreationDateTo()).toLocalDateTime()));
        } else if (Objects.nonNull(groupCriteriaDTO.getCreationDateFrom())) {
            filters.add(entityVariableWithDot + "createdAt >= :creationDateFrom");
            groupsTypedQueryParamBinders.add( t ->  t.setParameter("creationDateFrom", ZonedDateTime.parse(groupCriteriaDTO.getCreationDateFrom()).toLocalDateTime()));
            totalResultsQueryParamBinders.add( t ->  t.setParameter("creationDateFrom", ZonedDateTime.parse(groupCriteriaDTO.getCreationDateFrom()).toLocalDateTime()));
        } else if (Objects.nonNull(groupCriteriaDTO.getCreationDateTo())) {
            filters.add(entityVariableWithDot + "createdAt <= :creationDateTo");
            groupsTypedQueryParamBinders.add( t ->  t.setParameter("creationDateTo", ZonedDateTime.parse(groupCriteriaDTO.getCreationDateTo()).toLocalDateTime()));
            totalResultsQueryParamBinders.add( t ->  t.setParameter("creationDateTo", ZonedDateTime.parse(groupCriteriaDTO.getCreationDateTo()).toLocalDateTime()));
        }

        String fromPart = "FROM Group " + entityVariable + " ";
        String wherePart = "WHERE " + entityVariableWithDot + "deleted = false AND " + entityVariableWithDot + "applicationUser = '" + groupCriteriaDTO.getApplicationUser() + "' ";

        if (!filters.isEmpty()) {
            wherePart += "AND " + String.join(" AND ", filters);
        }

        String orderByPart = Util.getOrderByStatement(entityVariable, groupCriteriaDTO.getPageable().getSort(), APPLICABLE_SORT_FIELDS_PATH_VARIABLE_MAP);

        if (orderByPart.equals("")) {
            orderByPart = " ORDER BY " + entityVariableWithDot + "createdAt DESC";
        }

        // count total results for pagination reason
        TypedQuery<Long> totalResultsTypedQuery = entityManager.createQuery("SELECT COUNT(" + entityVariable + ") " + fromPart + wherePart, Long.class);

        TypedQuery<Group> groupsTypedQuery = entityManager.createQuery("SELECT " + entityVariable + " " + fromPart + wherePart + orderByPart, Group.class)
                .setMaxResults(groupCriteriaDTO.getPageable().getPageSize())
                .setFirstResult(groupCriteriaDTO.getPageable().getPageNumber() * groupCriteriaDTO.getPageable().getPageSize());

        return new RepositoryResultDTO<>(totalResultsTypedQuery, groupsTypedQuery);
    }
}
