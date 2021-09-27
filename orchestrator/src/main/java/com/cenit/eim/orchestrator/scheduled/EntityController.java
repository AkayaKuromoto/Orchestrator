package com.cenit.eim.orchestrator.scheduled;

import com.cenit.eim.orchestrator.business.CRIService;
import com.cenit.eim.orchestrator.business.PodService;
import com.cenit.eim.orchestrator.model.PodInst;
import com.cenit.eim.orchestrator.persistence.ContainerDefDAO;
import com.cenit.eim.orchestrator.persistence.ContainerInstDAO;
import com.cenit.eim.orchestrator.persistence.PodDefDAO;
import com.cenit.eim.orchestrator.persistence.PodInstDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import runtime.v1alpha2.PodSandbox;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class EntityController {

    private final PodInstDAO podInstDAO;
    private final PodService podService;
    private final CRIService criService;

    @Autowired
    public EntityController(PodInstDAO podInstDAO, PodService podService, CRIService criService) {
        this.podInstDAO = podInstDAO;
        this.podService = podService;
        this.criService = criService;
    }

    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void run() {
        List<PodSandbox> podSandboxList = criService.getAllPods().getItemsList();
        List<PodInst> podInstList = podInstDAO.findAll();
        podInstList.forEach(podInst -> {
            if(podSandboxList.stream().noneMatch(podSandbox -> podSandbox.getId().equals(podInst.getPodId()))){
                podService.deletePodInst(podInst.getPodId(), 1000);
            }
        });
    }
}