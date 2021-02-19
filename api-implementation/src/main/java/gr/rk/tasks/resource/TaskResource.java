package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.TasksApi;
import gr.rk.tasks.V1.models.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class TaskResource implements TasksApi {

    @Override
    public ResponseEntity<List<Task>> getTasks() {
        return ResponseEntity.ok(List.of(new Task().name("test").status("test")));
    }
}
