package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.GroupsApi;
import gr.rk.tasks.V1.dto.GroupDTO;
import gr.rk.tasks.V1.dto.UserDTO;
import gr.rk.tasks.entity.Group;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.mapper.GroupMapper;
import gr.rk.tasks.mapper.UserMapper;
import gr.rk.tasks.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class GroupResource implements GroupsApi {

    private final GroupService groupService;
    private final GroupMapper groupMapper;
    private final UserMapper userMapper;

    @Autowired
    public GroupResource(GroupService groupService, GroupMapper groupMapper, UserMapper userMapper) {
        this.groupService = groupService;
        this.groupMapper = groupMapper;
        this.userMapper = userMapper;
    }

    @Override
    public ResponseEntity<Page<GroupDTO>> getGroups(Pageable pageable, String name, String creationDateFrom, String creationDateTo) {
        List<GroupDTO> groupsDTO = new ArrayList<>();
        Page<GroupDTO> groupsDTOPage = new PageImpl<>(groupsDTO);

        Page<Group> groupsEntity = groupService.getGroups(pageable, name, creationDateFrom, creationDateTo);

        if (!groupsEntity.isEmpty()) {
            groupsDTOPage = groupMapper.toPageGroupsDTO(groupsEntity);
        }

        return ResponseEntity.ok(groupsDTOPage);
    }

    @Override
    public ResponseEntity<GroupDTO> getGroup(String name) {
        GroupDTO groupDTOResponse = new GroupDTO();

        Optional<Group> oGroupEntity = groupService.getGroup(name);

        if (oGroupEntity.isPresent()) {
            groupDTOResponse = groupMapper.toGroupDTO(oGroupEntity.get());
        }

        return ResponseEntity.ok(groupDTOResponse);
    }

    @Override
    public ResponseEntity<GroupDTO> createGroup(GroupDTO groupDTO) {
        Group groupEntity = groupMapper.toGroup(groupDTO);
        groupEntity = this.groupService.addGroup(groupEntity);

        GroupDTO groupDTOResponse = groupMapper.toGroupDTO(groupEntity);
        return ResponseEntity.ok(groupDTOResponse);
    }

    @Override
    public ResponseEntity<GroupDTO> addUserToGroup(String name, UserDTO userDTO) {
        User userEntity = userMapper.toUser(userDTO);
        Group group = groupService.addUserToGroup(name, userEntity);

        GroupDTO groupDTO = groupMapper.toGroupDTO(group);
        return ResponseEntity.ok(groupDTO);
    }

    @Override
    public ResponseEntity<Void> deleteGroup(String name) {
        groupService.deleteGroupLogical(name);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteUserFromGroup(String groupName, String username) {
        groupService.deleteUserFromGroupLogical(groupName, username);
        return ResponseEntity.ok().build();
    }

}
