package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.TasksApi;
import gr.rk.tasks.V1.models.Comment;
import gr.rk.tasks.V1.models.Task;
import gr.rk.tasks.V1.models.TaskDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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
    public ResponseEntity<Comment> addTaskComment(@Valid Comment body, UUID identifier) {
        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<TaskDetail> getTaskDetail(UUID identifier) {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setName("test");
        taskDetail.setIdentifier(UUID.randomUUID());
        taskDetail.setComments(
                List.of(new Comment().text("text comment")
                        .creationDate(LocalDateTime.now().atZone(ZoneId.of("UTC").normalized()).toString()))
        );
        return ResponseEntity.ok(taskDetail);
    }
}
