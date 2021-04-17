package gr.rk.tasks.service;

import gr.rk.tasks.entity.Group;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void addGroup(Group group) {
        groupRepository.save(group);
    }

    public void addUsersToGroup(Group group, List<User> users) {

        for(User user: users) {
            user.getGroups().add(group);
        }

        group.setUsers(users);
        groupRepository.save(group);
    }
}
