package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Spectator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpectatorRepository extends JpaRepository<Spectator, String> {
}
