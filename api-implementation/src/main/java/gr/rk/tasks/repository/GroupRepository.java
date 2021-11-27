package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, String>, GroupRepositoryCustom {
    Optional<Group> findByNameAndApplicationUserAndDeleted(String name, String applicationUser, boolean isDeleted);
    void deleteByName(String groupName);
}
