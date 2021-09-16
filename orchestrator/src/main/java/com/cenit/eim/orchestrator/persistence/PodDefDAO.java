package com.cenit.eim.orchestrator.persistence;

import com.cenit.eim.orchestrator.model.PodDef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PodDefDAO extends JpaRepository<PodDef, Long> {
    List<PodDef> findPodDefsByNameIn(List<String> names);

    PodDef findPodDefByName(String names);

    List<PodDef> findPodDefsByNamespace(String namespace);

    void deletePodDefByNameIn(List<String> names);


}
