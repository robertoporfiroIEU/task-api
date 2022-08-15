package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.ApplicationConfigurationsApi;
import gr.rk.tasks.V1.dto.ApplicationConfigurationDTO;
import gr.rk.tasks.entity.Configuration;
import gr.rk.tasks.mapper.ConfigurationMapper;
import gr.rk.tasks.service.ApplicationConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class ApplicationConfigurationResource implements ApplicationConfigurationsApi {

    private final ApplicationConfigurationService applicationConfigurationService;
    private final ConfigurationMapper configurationMapper;

    @Autowired
    public ApplicationConfigurationResource(ApplicationConfigurationService applicationConfigurationService, ConfigurationMapper configurationMapper) {
        this.applicationConfigurationService = applicationConfigurationService;
        this.configurationMapper = configurationMapper;
    }


    @Override
    @Secured("ROLE_READ_PROJECT")
    public ResponseEntity<List<ApplicationConfigurationDTO>> getApplicationConfigurations() {
        Set<Configuration> configurations = applicationConfigurationService.getApplicationConfigurations();
        return ResponseEntity.ok(new ArrayList<>(this.configurationMapper.toApplicationConfigurationDTOSet(configurations)));
    }
}

