package gr.rk.taskui.security;

import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;

import static org.springframework.security.config.Customizer.withDefaults;


import java.util.Objects;

@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Value("${spring.security.csrf}")
    private String csrf;
    Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Bean()
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange()
                .pathMatchers("/actuators/**")
                .permitAll()
             .and()
                .authorizeExchange()
                .pathMatchers("/auth/logout")
                .permitAll()
             .and()
                .authorizeExchange()
                .anyExchange()
                .authenticated()
             .and()
                .oauth2Client(withDefaults())
                .oauth2Login(withDefaults())
                .logout().disable();

        if (Objects.nonNull(csrf) && csrf.equals("enabled")) {
            logger.info("csrf enabled");
            http.csrf().csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()).and().cors();
        } else {
            logger.info("csrf disabled");
            http.csrf().disable().cors();
        }

        return http.build();
    }
}
