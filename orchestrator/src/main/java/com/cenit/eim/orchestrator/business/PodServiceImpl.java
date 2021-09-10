package com.cenit.eim.orchestrator.business;

import com.cenit.eim.orchestrator.model.*;
import com.cenit.eim.orchestrator.persistence.ContainerDefDAO;
import com.cenit.eim.orchestrator.persistence.ContainerInstDAO;
import com.cenit.eim.orchestrator.persistence.PodDefDAO;
import com.cenit.eim.orchestrator.persistence.PodInstDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cenit.eim.orchestrator.api.model.PodDefinitionCreateRequestDto;
import com.cenit.eim.orchestrator.api.model.ContainerDefinitionCreateRequestDto;
import runtime.v1alpha2.CreateContainerResponse;
import runtime.v1alpha2.RunPodSandboxResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PodServiceImpl implements PodService {

    private final CRIService criService;
    private final ContainerDefDAO containerDefDAO;
    private final ContainerInstDAO containerInstDAO;
    private final PodDefDAO podDefDAO;
    private final PodInstDAO podInstDAO;

    @Autowired
    public PodServiceImpl(CRIService criService, ContainerDefDAO containerDefDAO, ContainerInstDAO containerInstDAO, PodDefDAO podDefDAO, PodInstDAO podInstDAO){
        this.criService = criService;
        this.containerDefDAO = containerDefDAO;
        this.containerInstDAO = containerInstDAO;
        this.podDefDAO = podDefDAO;
        this.podInstDAO = podInstDAO;
    }

    @Override
    public List<PodDef> getAllPodDefinition() {
        return podDefDAO.findAll();
    }


    @Override
    public List<PodDef> getPodDefinitionsByNames(List<String> podDefinitionNames) {
        List<PodDef> result = new ArrayList<>();

        for(String podDefinitionName : podDefinitionNames){
            result.add(podDefDAO.findPodDefByName(podDefinitionName));
        }
        return result;
    }

    @Override
    public List<PodDef> getPodDefinitionsByNamespace(String podDefinitionNamespace) {
        return podDefDAO.findPodDefsByNamespace(podDefinitionNamespace);
    }

    @Override
    public void createPodDefinition(PodDefinitionCreateRequestDto podCreateRequest) {
        PodDef podDef = podDefDAO.save(new PodDef(podCreateRequest.getName(), podCreateRequest.getNamespace(), podCreateRequest.getCount(), Instant.now().getEpochSecond()));

        for(ContainerDefinitionCreateRequestDto containerRequest : podCreateRequest.getContainers()){
                containerDefDAO.save(
                        new ContainerDef(containerRequest.getName(),
                                podCreateRequest.getNamespace(),
                                containerRequest.getImageId(),
                                podDef)
                );
        }
    }

    @Override
    public PodInst createPodInst(PodDef podDef) {
        String uuid = getUUID();

        RunPodSandboxResponse podSandboxResponse = criService.createPod(podDef, uuid, 0);
        Map<ContainerDef, CreateContainerResponse> containerResponses = criService.createContainersInPod(podDef, podSandboxResponse.getPodSandboxId(), uuid, 0);

        PodInst podInst = podInstDAO.save(new PodInst(podSandboxResponse.getPodSandboxId(), PodState.NOT_READY, Instant.now().getEpochSecond(), podDef));

        for(Map.Entry<ContainerDef, CreateContainerResponse> entry : containerResponses.entrySet()){
            ContainerDef containerDef = entry.getKey();
            CreateContainerResponse containerResponse = entry.getValue();

            containerInstDAO.save(new ContainerInst(containerResponse.getContainerId(), ContainerState.CREATED, containerDef, podInst));
        }
        podInst.setState(PodState.READY);

        return podInst;
    }

    @Override
    public void deletePodDefinitionByNames(List<String> podDefinitionNames) {
        for(String podDefName : podDefinitionNames){
            try{
                PodDef podDef = podDefDAO.findPodDefByName(podDefName);

                for(ContainerDef containerDef : podDef.getContainers()){
                    for(ContainerInst containerInst : containerDef.getContainerInstances()){
                        containerInst.setContainer(null);
                    }
                    containerDefDAO.delete(containerDef);
                }

                for(PodInst podInst : podDef.getPodInstances()){
                    podInst.setPod(null);
                }
                podDefDAO.delete(podDef);
            }catch (NullPointerException e){
                System.err.println("Pod with name " + podDefName + " does not exist");
            }
        }
    }

    @Override
    public void deletePodDefinitionByNamespace(String podDefinitionNamespace) {
        List<PodDef> podDefList = podDefDAO.findPodDefsByNamespace(podDefinitionNamespace);

        for(PodDef podDef : podDefList){
            for(ContainerDef containerDef : podDef.getContainers()){
                containerDefDAO.delete(containerDef);
            }

            podDefDAO.delete(podDef);
        }
    }

    @Override
    public void deletePodInst(String podId, int timeout) {
        criService.deletePodById(podId, timeout);

        PodInst podInst = podInstDAO.findPodInstByPodId(podId);
        for(ContainerInst containerInst : podInst.getContainerInstances()){
            containerInstDAO.delete(containerInst);
        }
        podInstDAO.delete(podInst);
    }

    @Override
    public PodDef getPodDefinitionByName(String podDefinitionName) {
        return podDefDAO.findPodDefByName(podDefinitionName);
    }

    //    @Override
//    public void createContainerInst(String containerId, ContainerDef containerDef, PodInst podInst) {
//        containerInstDAO.save(new ContainerInst(containerId, ContainerState.CREATED, containerDef, podInst));
//    }
//
//    @Override
//    public void deletePodById(List<String> podIds) {
//        for(String podId : podIds){
//            podDefDAO.delete(podDefDAO.getPodByPodId(podId));
//        }
//    }
//
//    @Override
//    public void deletePodByNamespace(String podNamespace) {
//        List<PodDef> podDefList = getPodsByNamespace(podNamespace);
//
//        for(PodDef podDef : podDefList){
//            podDefDAO.delete(podDef);
//        }
//    }

    private String getUUID(){
        return UUID.randomUUID().toString();
    }
}
