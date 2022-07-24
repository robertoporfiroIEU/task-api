package gr.rk.tasks.service;

import gr.rk.tasks.dto.GroupCriteriaDTO;
import gr.rk.tasks.entity.Group;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.exception.ApplicationException;
import gr.rk.tasks.exception.i18n.I18nErrorMessage;
import gr.rk.tasks.repository.GroupRepository;
import gr.rk.tasks.repository.UserRepository;
import gr.rk.tasks.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserPrincipal userPrincipal;

    @Value("${applicationConfigurations.groupService.pageMaxSize: 25}")
    private int maxSize;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserRepository userRepository, UserPrincipal userPrincipal) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.userPrincipal = userPrincipal;
    }

    public Page<Group> getGroups(GroupCriteriaDTO groupCriteriaDTO) {
        Pageable page = groupCriteriaDTO.getPageable();

        if (groupCriteriaDTO.getPageable().getPageSize() > maxSize) {
            page = PageRequest.of(groupCriteriaDTO.getPageable().getPageNumber(), maxSize, groupCriteriaDTO.getPageable().getSort());
        }

        groupCriteriaDTO.setApplicationUser(userPrincipal.getClientName());
        groupCriteriaDTO.setPageable(page);

        Page<Group> groups =  groupRepository.findGroupDynamicJPQL(groupCriteriaDTO);

        // Remove the deleted users
        groups.forEach( g ->
                g.setUsers(g.getUsers().stream()
                        .filter( user -> !user.isDeleted())
                        .collect(Collectors.toSet())
                )
        );
        return groups;
    }

    public Optional<Group> getGroup(String name) {
        return groupRepository.findByNameAndApplicationUserAndDeleted(name, userPrincipal.getClientName(), false);
    }

    public Group addGroup(Group group) {
        return groupRepository.save(group);
    }

    @Transactional
    public Group addUserToGroup(String groupName, User user) {
        // validations
        Optional<Group> oGroup = groupRepository.findByNameAndApplicationUserAndDeleted(groupName, userPrincipal.getClientName(), false);

        if (oGroup.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.GROUP_NOT_FOUND);
        }

        Optional<User> oUser = userRepository.findByUsernameAndApplicationUserAndDeleted(user.getUsername(), userPrincipal.getClientName(), false);

        if (oUser.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.USER_NOT_FOUND);
        }

        Group group = oGroup.get();
        user = oUser.get();
        String username = user.getUsername();

        // if the user exist in group do not insert
        boolean isPresent = group.getUsers().stream().anyMatch(userItem -> username.equals(userItem.getUsername()));
        if (isPresent) {
            return group;
        }

        user.getGroups().add(group);
        group.getUsers().add(user);
        return group;
    }

    @Transactional
    public void deleteGroup(String groupName) {
        groupRepository.deleteByName(groupName);
    }

    @Transactional
    public void deleteGroupLogical(String name) {
        Optional<Group> oGroup = groupRepository.findByNameAndApplicationUserAndDeleted(name, userPrincipal.getClientName(), false);

        if (oGroup.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.GROUP_NOT_FOUND);
        }

        Group group = oGroup.get();
        group.setDeleted(true);
    }

    @Transactional
    public void deleteUserFromGroupLogical(String groupName, String username) {
        Optional<Group> oGroup = groupRepository.findByNameAndApplicationUserAndDeleted(groupName, userPrincipal.getClientName(), false);

        if (oGroup.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.GROUP_NOT_FOUND);
        }

        Group group = oGroup.get();
        group.getUsers().removeIf( u -> {
            if ( u.getUsername().equals(username)) {
                u.getGroups().removeIf(g -> g.getName().equals(groupName));
                return true;
            }
            return false;
        });
    }
}
