package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Spectator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpectatorRepository extends JpaRepository<Spectator, Long> {
    Optional<Spectator> findSpectatorByIdentifier(String identifier);
}
