package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.TasksApi;
import gr.rk.tasks.V1.models.Comment;
import gr.rk.tasks.V1.models.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;


@RestController
public class TaskResource implements TasksApi {

    @Override
    public ResponseEntity<List<Task>> getTasks() {
        return ResponseEntity.ok(List.of(new Task().name("test").status("test")));
    }

    @Override
    public ResponseEntity<List<Comment>> getTaskComments(UUID identifier) {
        return ResponseEntity.ok(List.of(new Comment().text("testComment")
        .creationDate(LocalDateTime.now().atZone(ZoneId.of("UTC").normalized()).toString())));
    }
}
