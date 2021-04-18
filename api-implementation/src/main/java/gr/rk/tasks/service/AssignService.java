package gr.rk.tasks.service;

import gr.rk.tasks.entity.Assign;
import gr.rk.tasks.repository.AssignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignService {

    private final AssignRepository assignRepository;

    @Autowired
    public AssignService(AssignRepository assignRepository) {
        this.assignRepository = assignRepository;
    }

    public void addAssign(Assign assign) {
        assignRepository.save(assign);
    }
}
