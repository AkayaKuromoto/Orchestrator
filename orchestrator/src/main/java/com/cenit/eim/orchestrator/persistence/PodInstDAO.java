package com.cenit.eim.orchestrator.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cenit.eim.orchestrator.model.PodInst;

public interface PodInstDAO extends JpaRepository<PodInst, Long> {
    PodInst findPodInstByPodId(String podId);
}
