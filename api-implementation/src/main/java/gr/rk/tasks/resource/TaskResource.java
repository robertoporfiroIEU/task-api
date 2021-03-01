package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.TasksApi;
import gr.rk.tasks.V1.models.Assign;
import gr.rk.tasks.V1.models.Comment;
import gr.rk.tasks.V1.models.TaskDetail;
import model.EnvelopeResponse;
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
    public ResponseEntity<Assign> addAssign(UUID identifier, @Valid Assign assign) {
        return null;
    }

    @Override
    public ResponseEntity<Comment> addTaskComment(UUID identifier, @Valid Comment comment) {
        return null;
    }

    @Override
    public ResponseEntity<EnvelopeResponse<TaskDetail>> getTaskDetail(UUID identifier) {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setName("test");
        taskDetail.setIdentifier(UUID.randomUUID());
        taskDetail.setComments(
                List.of(new Comment().text("text comment")
                        .creationDate(LocalDateTime.now().atZone(ZoneId.of("UTC").normalized()).toString()))
        );
        return ResponseEntity.ok(new EnvelopeResponse.Builder<TaskDetail>().withData(taskDetail).build());
    }

    @Override
    public ResponseEntity<EnvelopeResponse> getTasks() {
        return null;
    }

}
