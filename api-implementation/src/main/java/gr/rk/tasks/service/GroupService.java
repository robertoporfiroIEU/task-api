package gr.rk.tasks.service;

import gr.rk.tasks.entity.Group;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.exception.i18n.GroupNotFoundException;
import gr.rk.tasks.exception.i18n.I18nErrorMessage;
import gr.rk.tasks.exception.i18n.UserNotFoundException;
import gr.rk.tasks.repository.GroupRepository;
import gr.rk.tasks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Value("${applicationConfigurations.groupService.pageMaxSize: 25}")
    private int maxSize;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
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

    @Transactional
    public Group addUserToGroup(String groupName, User user) {
        Optional<Group> oGroup = groupRepository.findById(groupName);

        if (oGroup.isEmpty()) {
            throw new GroupNotFoundException(I18nErrorMessage.GROUP_NOT_FOUND);
        }

        Optional<User> oUser = userRepository.findById(user.getUsername());

        if (oUser.isEmpty()) {
            throw new UserNotFoundException(I18nErrorMessage.USER_NOT_FOUND);
        }

        Group group = oGroup.get();
        user = oUser.get();
        String username = user.getUsername();

        // if the user exist in group do not insert
        if (Objects.nonNull(group.getUsers().stream()
                .filter(userItem -> userItem.getUsername().equals(username))
                .findAny()
                .orElse(null))
        ) {
            return group;
        }

        user.getGroups().add(group);
        group.getUsers().add(user);
        return group;
    }
}
