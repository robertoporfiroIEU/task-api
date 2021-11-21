package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Assign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AssignRepository extends JpaRepository<Assign, Long> {
    Optional<Assign> findAssignByIdentifierAndApplicationUserAndDeleted(String identifier, String applicationUser, boolean deleted);
    Page<Assign> findAssignByTaskIdentifierAndApplicationUserAndDeleted(String identifier, String applicationUser, boolean deleted, Pageable pageable);
}
