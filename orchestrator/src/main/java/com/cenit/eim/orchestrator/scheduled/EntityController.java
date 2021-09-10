package com.cenit.eim.orchestrator.scheduled;

import com.cenit.eim.orchestrator.business.CRIService;
import com.cenit.eim.orchestrator.business.PodService;
import com.cenit.eim.orchestrator.model.ContainerDef;
import com.cenit.eim.orchestrator.model.PodDef;
import com.cenit.eim.orchestrator.model.PodInst;
import com.cenit.eim.orchestrator.persistence.ContainerDefDAO;
import com.cenit.eim.orchestrator.persistence.ContainerInstDAO;
import com.cenit.eim.orchestrator.persistence.PodDefDAO;
import com.cenit.eim.orchestrator.persistence.PodInstDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import runtime.v1alpha2.Container;
import runtime.v1alpha2.ListContainersResponse;
import runtime.v1alpha2.PodSandbox;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class EntityController {

    private final PodInstDAO podInstDAO;
    private final PodDefDAO podDefDAO;
    private final ContainerInstDAO containerInstDAO;
    private final ContainerDefDAO containerDefDAO;

    private final PodService podService;
    private final CRIService criService;

    @Autowired
    public EntityController(PodInstDAO podInstDAO, PodDefDAO podDefDAO, ContainerInstDAO containerInstDAO, ContainerDefDAO containerDefDAO, PodService podService, CRIService criService) {
        this.podInstDAO = podInstDAO;
        this.podDefDAO = podDefDAO;
        this.containerInstDAO = containerInstDAO;
        this.containerDefDAO = containerDefDAO;
        this.podService = podService;
        this.criService = criService;
    }

    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void run() {
        List<PodSandbox> podSandboxList = criService.getAllPods().getItemsList();
        List<PodInst> podInstList = podInstDAO.findAll();

        for(PodInst podInst : podInstList){
            boolean hasPodInstance = false;
            for (PodSandbox podSandbox : podSandboxList){
                hasPodInstance |= podInst.getPodId().equals(podSandbox.getId());
            }
            if(!hasPodInstance){
                podService.deletePodInst(podInst.getPodId(), 1000);
            }
        }
    }
}