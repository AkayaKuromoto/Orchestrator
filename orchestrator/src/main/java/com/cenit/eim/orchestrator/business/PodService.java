package com.cenit.eim.orchestrator.business;

import com.cenit.eim.orchestrator.model.PodDef;
import com.cenit.eim.orchestrator.api.model.PodDefinitionCreateRequestDto;
import com.cenit.eim.orchestrator.model.PodInst;

import java.util.List;

public interface PodService {

    /* TODO Optimize */
    List<PodDef> getAllPodDefinition();

    /* TODO Optimize */
    List<PodDef> getPodDefinitionsByNames(List<String> podDefinitionNames);

    /* TODO Optimize */
    List<PodDef> getPodDefinitionsByNamespace(String podDefinitionNamespace);

    void createPodDefinition(PodDefinitionCreateRequestDto podCreateRequest);

    PodInst createPodInst(PodDef podDef);

    void deletePodDefinitionByNames(List<String> podDefinitionNames);

    void deletePodDefinitionByNamespace(String podDefinitionNamespace);

    void deletePodInst(String podId, int timeout);

    PodDef getPodDefinitionByName(String podDefinitionName);

//    void createContainerInst(String containerId, ContainerDef containerDef, PodInst podInst);

//    /* TODO Optimize */
//    void deletePodById(List<String> podIds);
//
//    /* TODO Optimize */
//    void deletePodByNamespace(String podNamespace);
}
