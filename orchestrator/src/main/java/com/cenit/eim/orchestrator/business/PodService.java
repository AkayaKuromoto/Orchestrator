package com.cenit.eim.orchestrator.business;

import com.cenit.eim.orchestrator.model.PodDef;
import com.cenit.eim.orchestrator.api.model.PodDefinitionCreateRequestDto;
import com.cenit.eim.orchestrator.model.PodInst;

import java.util.List;

public interface PodService {

    List<PodDef> getAllPodDefinition();

    List<PodDef> getPodDefinitionsByNames(List<String> podDefinitionNames);

    List<PodDef> getPodDefinitionsByNamespace(String podDefinitionNamespace);

    void createPodDefinition(PodDefinitionCreateRequestDto podCreateRequest);

    PodInst createPodInst(PodDef podDef);

    void deletePodDefinitionByNames(List<String> podDefinitionNames);

    void deletePodDefinitionByNamespace(String podDefinitionNamespace);

    void deletePodInst(String podId, int timeout);

    PodDef getPodDefinitionByName(String podDefinitionName);

    void deletePodDefinitionByName(String podDefinitionName);
}
