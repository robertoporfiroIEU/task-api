package gr.rk.tasks.service;

import gr.rk.tasks.entity.Spectator;
import gr.rk.tasks.repository.SpectatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpectatorService {

    private final SpectatorRepository spectatorRepository;

    @Autowired
    public SpectatorService(SpectatorRepository spectatorRepository) {
        this.spectatorRepository = spectatorRepository;
    }

    public void addSpectator(Spectator spectator) {
        spectatorRepository.save(spectator);
    }
}
