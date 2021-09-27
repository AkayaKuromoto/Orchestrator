package com.cenit.eim.orchestrator.persistence;

import com.cenit.eim.orchestrator.model.ContainerInst;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerInstDAO extends JpaRepository<ContainerInst, Long> {
}
