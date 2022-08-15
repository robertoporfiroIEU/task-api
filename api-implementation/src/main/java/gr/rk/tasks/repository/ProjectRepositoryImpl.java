package gr.rk.tasks.repository;

import gr.rk.tasks.dto.ProjectCriteriaDTO;
import gr.rk.tasks.dto.RepositoryResultDTO;
import gr.rk.tasks.entity.Project;
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
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    private static final Map<String, String> APPLICABLE_SORT_FIELDS_PATH_VARIABLE_MAP = Map.of(
            "identifier", "identifier",
            "name", "name",
            "createdAt", "createdAt",
            "createdBy", "createdBy"
    );

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Project saveProject(Project project) {
        String lastProjectIdSQL = "SELECT p.id from Project p order by p.id desc";
        // Get the last id of the project entity and increment by one
        long lastId;
        try {
            lastId = entityManager.createQuery(lastProjectIdSQL, Long.class).setMaxResults(1).getSingleResult();
            lastId ++;
        } catch (NoResultException e){
            lastId = 0;
        }
        project.setIdentifier(project.getPrefixIdentifier() + "-" + lastId);
        entityManager.persist(project);
        return project;
    }

    @Override
    public Page<Project> findProjectsDynamicJPQL(ProjectCriteriaDTO projectCriteriaDTO) {
        List<Consumer<TypedQuery<Project>>> projectsTypedQueryParamBinders = new ArrayList<>();
        List<Consumer<TypedQuery<Long>>> totalResultsQueryParamBinders = new ArrayList<>();

        RepositoryResultDTO<Project> repositoryResultDTO = findProjects(projectCriteriaDTO, projectsTypedQueryParamBinders, totalResultsQueryParamBinders);

        TypedQuery<Project> projectsTypedQuery = repositoryResultDTO.getResultTypedQuery();
        TypedQuery<Long> totalResultsQuery = repositoryResultDTO.getTotalResultTypedQuery();

        projectsTypedQueryParamBinders.forEach(typedQueryConsumer -> typedQueryConsumer.accept(projectsTypedQuery));
        totalResultsQueryParamBinders.forEach(typedQueryConsumer -> typedQueryConsumer.accept(totalResultsQuery));

        return new PageImpl<>(projectsTypedQuery.getResultList(), projectCriteriaDTO.getPageable(), totalResultsQuery.getSingleResult());

    }

    private RepositoryResultDTO<Project> findProjects(
            ProjectCriteriaDTO projectCriteriaDTO,
            List<Consumer<TypedQuery<Project>>> projectsTypedQueryParamBinders,
            List<Consumer<TypedQuery<Long>>> totalResultsQueryParamBinders
            ) {
        List<String> filters = new ArrayList<>();
        String entityVariable = "p";
        String entityVariableWithDot = entityVariable + ".";

        // create where clause
        if (Objects.nonNull(projectCriteriaDTO.getIdentifier())) {
            filters.add(entityVariableWithDot + "identifier = :identifier");
            projectsTypedQueryParamBinders.add((projectTypedQuery -> projectTypedQuery.setParameter("identifier", projectCriteriaDTO.getIdentifier())));
            totalResultsQueryParamBinders.add((projectTypedQuery -> projectTypedQuery.setParameter("identifier", projectCriteriaDTO.getIdentifier())));
        }

        if (Objects.nonNull(projectCriteriaDTO.getName())) {
            filters.add(entityVariableWithDot + "name like :name");
            projectsTypedQueryParamBinders.add((projectTypedQuery -> projectTypedQuery.setParameter("name", "%" + projectCriteriaDTO.getName() + "%")));
            totalResultsQueryParamBinders.add((projectTypedQuery -> projectTypedQuery.setParameter("name", "%" + projectCriteriaDTO.getName() + "%")));
        }

        if (Objects.nonNull(projectCriteriaDTO.getCreatedBy())) {
            filters.add(entityVariableWithDot + "createdBy like :createdBy");
            projectsTypedQueryParamBinders.add((projectTypedQuery -> projectTypedQuery.setParameter("createdBy", "%" + projectCriteriaDTO.getCreatedBy() + "%")));
            totalResultsQueryParamBinders.add((projectTypedQuery -> projectTypedQuery.setParameter("createdBy", "%" + projectCriteriaDTO.getCreatedBy() + "%")));
        }

        if (Objects.nonNull(projectCriteriaDTO.getCreationDateFrom()) && Objects.nonNull(projectCriteriaDTO.getCreationDateTo())) {
            filters.add(entityVariableWithDot + "createdAt >= :creationDateFrom AND " +  entityVariableWithDot + "createdAt <= :creationDateTo");
            projectsTypedQueryParamBinders.add((projectTypedQuery -> projectTypedQuery.setParameter("creationDateFrom", ZonedDateTime.parse(projectCriteriaDTO.getCreationDateFrom()).toLocalDateTime())));
            projectsTypedQueryParamBinders.add((projectTypedQuery -> projectTypedQuery.setParameter("creationDateTo", ZonedDateTime.parse(projectCriteriaDTO.getCreationDateTo()).toLocalDateTime())));
            totalResultsQueryParamBinders.add((projectTypedQuery -> projectTypedQuery.setParameter("creationDateFrom", ZonedDateTime.parse(projectCriteriaDTO.getCreationDateFrom()).toLocalDateTime())));
            totalResultsQueryParamBinders.add((projectTypedQuery -> projectTypedQuery.setParameter("creationDateTo", ZonedDateTime.parse(projectCriteriaDTO.getCreationDateTo()).toLocalDateTime())));
        } else if (Objects.nonNull(projectCriteriaDTO.getCreationDateFrom())) {
            filters.add(entityVariableWithDot + "createdAt >= :creationDateFrom");
            projectsTypedQueryParamBinders.add((projectTypedQuery -> projectTypedQuery.setParameter("creationDateFrom", ZonedDateTime.parse(projectCriteriaDTO.getCreationDateFrom()).toLocalDateTime())));
            totalResultsQueryParamBinders.add((projectTypedQuery -> projectTypedQuery.setParameter("creationDateFrom", ZonedDateTime.parse(projectCriteriaDTO.getCreationDateFrom()).toLocalDateTime())));
        } else if (Objects.nonNull(projectCriteriaDTO.getCreationDateTo())) {
            filters.add(entityVariableWithDot + "createdAt <= :creationDateTo");
            projectsTypedQueryParamBinders.add((projectTypedQuery -> projectTypedQuery.setParameter("creationDateTo", ZonedDateTime.parse(projectCriteriaDTO.getCreationDateTo()).toLocalDateTime())));
            totalResultsQueryParamBinders.add((projectTypedQuery -> projectTypedQuery.setParameter("creationDateTo", ZonedDateTime.parse(projectCriteriaDTO.getCreationDateTo()).toLocalDateTime())));
        }

        String fromPart = "FROM Project " + entityVariable + " ";
        String wherePart = "WHERE " + entityVariableWithDot + "deleted = false AND "
                + entityVariableWithDot + "applicationUser = '" + projectCriteriaDTO.getApplicationUser() + "' ";

        if (!filters.isEmpty()) {
            wherePart += "AND " + String.join(" AND ", filters);
        }

        String orderByPart = Util.getOrderByStatement(entityVariable, projectCriteriaDTO.getPageable().getSort(), APPLICABLE_SORT_FIELDS_PATH_VARIABLE_MAP);

        if (orderByPart.equals("")) {
            orderByPart = " ORDER BY " + entityVariableWithDot + "createdAt DESC";
        }

        // count total results for pagination reason
        TypedQuery<Long> totalResultsTypedQuery = entityManager.createQuery("SELECT COUNT( " + entityVariable +") " + fromPart + wherePart, Long.class);

        TypedQuery<Project> projectsTypedQuery = entityManager.createQuery("SELECT " + entityVariable + " " + fromPart + wherePart + orderByPart, Project.class)
                .setMaxResults(projectCriteriaDTO.getPageable().getPageSize())
                .setFirstResult(projectCriteriaDTO.getPageable().getPageNumber() * projectCriteriaDTO.getPageable().getPageSize());

        return new RepositoryResultDTO<>(totalResultsTypedQuery, projectsTypedQuery);
    }
}
