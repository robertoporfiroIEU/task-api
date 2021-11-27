package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupRepositoryCustom {

    Page<Group> findGroupDynamicJPQL(Pageable pageable, String name, String creationDateFrom, String creationDateTo, String applicationUser);
}
