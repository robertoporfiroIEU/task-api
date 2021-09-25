package gr.rk.tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@SpringBootApplication
@EnableSwagger2
public class TasksApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TasksApiApplication.class, args);
    }

    @Primary
    @Bean
    public SwaggerResourcesProvider swaggerResourcesProvider() {
        return () -> {
            SwaggerResource wsResource = new SwaggerResource();
            // This file must exist in the api-definition module. This bean has access to the resources of the other module due to the pom dependency
            wsResource.setName("Task API");
            wsResource.setLocation("/V1/openapi.yaml");
            return List.of(wsResource);
        };
    }

}
