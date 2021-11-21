package gr.rk.tasks.repository;

import gr.rk.tasks.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRepositoryImpl implements UserRepositoryCustom{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<User> findUsersDynamicJPQL(Pageable pageable, String name, String email, String applicationUser) {
        String baseJPQL = "SELECT u from User u where u.deleted = false and u.applicationUser = '" + applicationUser + "' ";
        String countJPQL = "SELECT count(u.username) from User u where u.deleted = false and u.applicationUser = '" + applicationUser + "'";
        String whereClause = "";

        // count total results for pagination reason
        long totalResults = entityManager.createQuery(countJPQL, Long.class).getSingleResult();

        List<String> filters = getFilters(name, email);

        if (!filters.isEmpty()) {
            whereClause += "and " + String.join("AND ", filters);
        }

        TypedQuery<User> userTypedQuery = entityManager.createQuery(baseJPQL + whereClause, User.class)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize());

        // bind the parameters to avoid sql injections
        if (!filters.isEmpty()) {
            if (Objects.nonNull(name)) {
                userTypedQuery.setParameter("username", "%" + name + "%");
            }

            if (Objects.nonNull(email)) {
                userTypedQuery.setParameter("email", "%" + email + "%");
            }
        }

        return new PageImpl<>(userTypedQuery.getResultList(), pageable, totalResults);
    }

    private List<String> getFilters(String name, String email) {
        List<String> filters = new ArrayList<>();

        // create where clause
        if (Objects.nonNull(name)) {
            filters.add("u.username like :username");
        }

        if (Objects.nonNull(email)) {
            filters.add("u.email like :email");
        }
        return filters;
    }
}
