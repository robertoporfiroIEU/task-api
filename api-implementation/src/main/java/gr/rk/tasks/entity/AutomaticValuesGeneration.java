package gr.rk.tasks.entity;

import javax.persistence.PrePersist;

public interface AutomaticValuesGeneration {

    @PrePersist
    void generateAutomatedValues();
}
