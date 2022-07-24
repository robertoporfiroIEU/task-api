package gr.rk.taskui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication(exclude = ReactiveUserDetailsServiceAutoConfiguration.class)
public class TaskUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskUiApplication.class, args);
    }

}
