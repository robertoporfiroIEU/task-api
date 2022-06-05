package gr.rk.tasks.configuration;

import gr.rk.tasks.security.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfiguration {

    @Bean
    public UserPrincipal getUserPrincipal() {
       return new UserPrincipal("angular-task-api", "rafail", List.of("task-api-role"));
    }
}
