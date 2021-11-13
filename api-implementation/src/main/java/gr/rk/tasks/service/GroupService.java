package gr.rk.tasks.service;

import gr.rk.tasks.entity.Group;
import gr.rk.tasks.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    @Value("${applicationConfigurations.groupService.pageMaxSize: 25}")
    private int maxSize;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Page<Group> getGroups(Pageable pageable, String name, String creationDateFrom, String creationDateTo) {
        Pageable page = pageable;

        if (pageable.getPageSize() > maxSize) {
            page = PageRequest.of(pageable.getPageNumber(), maxSize, pageable.getSort());
        }

        return groupRepository.findGroupDynamicJPQL(page, name, creationDateFrom, creationDateTo);
    }

    public Optional<Group> getGroup(String name) {
        return groupRepository.findById(name);
    }

    public Group addGroup(Group group) {
        return groupRepository.save(group);
    }

    public void deleteGroup(String groupName) {
        groupRepository.deleteByName(groupName);
    }

}
