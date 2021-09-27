package com.cenit.eim.orchestrator.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cenit.eim.orchestrator.model.PodInst;

import java.util.List;

public interface PodInstDAO extends JpaRepository<PodInst, Long> {
    PodInst findPodInstByPodId(String podId);

    List<PodInst> findPodInstsByPodDefIsNull();
}
