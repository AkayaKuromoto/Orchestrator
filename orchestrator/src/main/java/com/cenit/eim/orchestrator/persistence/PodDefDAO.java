package com.cenit.eim.orchestrator.persistence;

import com.cenit.eim.orchestrator.model.PodDef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/*FIXME methoden die holen was ich brauche*/
public interface PodDefDAO extends JpaRepository<PodDef, Long> {
    PodDef findPodDefByName(String names);

    List<PodDef> findPodDefsByNamespace(String namespace);
}
