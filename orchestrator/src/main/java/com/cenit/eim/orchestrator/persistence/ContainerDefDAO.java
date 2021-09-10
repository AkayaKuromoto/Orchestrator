package com.cenit.eim.orchestrator.persistence;

import com.cenit.eim.orchestrator.model.ContainerDef;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerDefDAO extends JpaRepository<ContainerDef, Long> {
}
