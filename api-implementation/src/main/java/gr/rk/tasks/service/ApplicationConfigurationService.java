package gr.rk.tasks.service;

import gr.rk.tasks.entity.Configuration;
import gr.rk.tasks.repository.ApplicationConfigurationRepository;
import gr.rk.tasks.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ApplicationConfigurationService {

    private ApplicationConfigurationRepository applicationConfigurationRepository;
    private UserPrincipal userPrincipal;

    @Autowired
    public ApplicationConfigurationService(ApplicationConfigurationRepository applicationConfigurationRepository, UserPrincipal userPrincipal) {
        this.applicationConfigurationRepository = applicationConfigurationRepository;
        this.userPrincipal = userPrincipal;
    }

    public Set<Configuration> getApplicationConfigurations() {
        return applicationConfigurationRepository.findDefaultAndUserApplicationConfigurations(this.userPrincipal.getApplicationUser());
    }
}
