package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.GroupsApi;
import gr.rk.tasks.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GroupResource implements GroupsApi {

    private final GroupService groupService;

    @Autowired
    public GroupResource(GroupService groupService) {
        this.groupService = groupService;
    }


    @Override
    public ResponseEntity<List<String>> getGroups(String name) {
       return ResponseEntity.ok(groupService.getGroups(name));
    }
}
