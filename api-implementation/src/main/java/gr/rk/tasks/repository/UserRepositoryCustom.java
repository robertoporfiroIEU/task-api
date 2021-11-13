package gr.rk.tasks.repository;

import gr.rk.tasks.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<User> findUsersDynamicJPQL(Pageable pageable, String name, String email);
}
