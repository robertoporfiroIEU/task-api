package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ApplicationConfigurationRepository extends JpaRepository<Configuration, Long> {

    @Query("SELECT c FROM Configuration c where c.applicationUser is null ORDER BY c.configurationName, c.weight")
    Set<Configuration> findDefaultApplicationConfigurations();


    @Query("SELECT c FROM Configuration c where c.applicationUser is null or c.applicationUser = :applicationUser ORDER BY c.configurationName, c.weight")
    Set<Configuration> findDefaultAndUserApplicationConfigurations(String applicationUser);
}
