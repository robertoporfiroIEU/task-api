package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Assign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignRepository extends JpaRepository<Assign, String> {
}
