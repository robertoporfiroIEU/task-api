package gr.rk.tasks.repository;

import gr.rk.tasks.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    void deleteByUsername(String username);
    boolean existsByUsername(String username);
}
