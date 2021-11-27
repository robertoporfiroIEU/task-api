package gr.rk.tasks.repository;

import gr.rk.tasks.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, UserRepositoryCustom {
    Optional<User> findByUsernameAndApplicationUserAndDeleted(String username, String applicationUser, boolean isDeleted);
    void deleteByUsername(String username);
}
