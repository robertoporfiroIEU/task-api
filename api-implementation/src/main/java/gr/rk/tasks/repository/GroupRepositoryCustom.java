package gr.rk.tasks.repository;

import gr.rk.tasks.dto.GroupCriteriaDTO;
import gr.rk.tasks.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupRepositoryCustom {

    Page<Group> findGroupDynamicJPQL(GroupCriteriaDTO groupCriteriaDTO);
}
