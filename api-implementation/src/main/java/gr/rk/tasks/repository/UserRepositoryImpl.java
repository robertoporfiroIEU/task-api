package gr.rk.tasks.repository;

import gr.rk.tasks.dto.RepositoryResultDTO;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.util.Util;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class UserRepositoryImpl implements UserRepositoryCustom{
    private static final Map<String, String> APPLICABLE_SORT_FIELDS_PATH_VARIABLE_MAP = Map.of(
            "name", "username",
            "email", "email"
    );
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<User> findUsersDynamicJPQL(Pageable pageable, String name, String email, String applicationUser) {
        List<Consumer<TypedQuery<User>>> usersTypedQueryParamBinders = new ArrayList<>();
        List<Consumer<TypedQuery<Long>>> totalResultsQueryParamBinders = new ArrayList<>();


        RepositoryResultDTO<User> repositoryResultDTO = findUsers(
                name,
                email,
                applicationUser,
                pageable,
                usersTypedQueryParamBinders,
                totalResultsQueryParamBinders
        );

        TypedQuery<User> usersTypedQuery = repositoryResultDTO.getResultTypedQuery();
        TypedQuery<Long> totalResultsQuery = repositoryResultDTO.getTotalResultTypedQuery();

        usersTypedQueryParamBinders.forEach(typedQueryConsumer -> typedQueryConsumer.accept(usersTypedQuery));
        totalResultsQueryParamBinders.forEach(typedQueryConsumer -> typedQueryConsumer.accept(totalResultsQuery));

        return new PageImpl<>(usersTypedQuery.getResultList(), pageable, totalResultsQuery.getSingleResult());
    }

    private RepositoryResultDTO<User> findUsers(
            String name,
            String email,
            String applicationUser,
            Pageable pageable,
            List<Consumer<TypedQuery<User>>> usersTypedQueryParamBinders,
            List<Consumer<TypedQuery<Long>>> totalResultsQueryParamBinders
    ) {
        List<String> filters = new ArrayList<>();
        String entityVariable = "u";
        String entityVariableWithDot = entityVariable + ".";

        // create where clause
        if (Objects.nonNull(name)) {
            filters.add("u.username like :username");
            usersTypedQueryParamBinders.add( userTypedQuery ->  userTypedQuery.setParameter("username", "%" + name + "%"));
            totalResultsQueryParamBinders.add( userTypedQuery ->  userTypedQuery.setParameter("username", "%" + name + "%"));
        }

        if (Objects.nonNull(email)) {
            filters.add("u.email like :email");
            usersTypedQueryParamBinders.add( userTypedQuery ->  userTypedQuery.setParameter("email", "%" + email + "%"));
            totalResultsQueryParamBinders.add( userTypedQuery ->  userTypedQuery.setParameter("email", "%" + email + "%"));

        }

        String fromPart = "FROM User " + entityVariable + " ";
        String wherePart = "WHERE " + entityVariableWithDot + "deleted = false AND " + entityVariableWithDot + "applicationUser = '" + applicationUser + "' ";

        if (!filters.isEmpty()) {
            wherePart += "AND " + String.join(" AND ", filters);
        }

        String orderByPart = Util.getOrderByStatement(entityVariable, pageable.getSort(), APPLICABLE_SORT_FIELDS_PATH_VARIABLE_MAP);

        if (orderByPart.equals("")) {
            orderByPart = " ORDER BY " + entityVariableWithDot + "name DESC";
        }

        // count total results for pagination reason
        TypedQuery<Long> totalResultsTypedQuery = entityManager.createQuery("SELECT COUNT(" + entityVariable + ") " + fromPart + wherePart, Long.class);

        TypedQuery<User> usersTypedQuery = entityManager.createQuery("SELECT " + entityVariable + " " + fromPart + wherePart + orderByPart, User.class)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize());

        return new RepositoryResultDTO<>(totalResultsTypedQuery, usersTypedQuery);
    }
}
