package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Assign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignRepository extends JpaRepository<Assign, Long> {
    Page<Assign> findAssignByTaskIdentifierAndApplicationUser(String identifier, String applicationUser, Pageable pageable);
}
