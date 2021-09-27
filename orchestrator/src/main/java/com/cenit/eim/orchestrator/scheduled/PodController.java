package com.cenit.eim.orchestrator.scheduled;

import com.cenit.eim.orchestrator.business.PodService;
import com.cenit.eim.orchestrator.model.PodDef;
import com.cenit.eim.orchestrator.model.PodInst;
import com.cenit.eim.orchestrator.persistence.PodDefDAO;
import com.cenit.eim.orchestrator.persistence.PodInstDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class PodController {

    private final PodDefDAO podDefDAO;
    private final PodInstDAO podInstDAO;
    private final PodService podService;

    @Autowired
    public PodController(PodDefDAO podDefDAO, PodInstDAO podInstDAO, PodService podService) {
        this.podDefDAO = podDefDAO;
        this.podInstDAO = podInstDAO;
        this.podService = podService;
    }

    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void run() {
        checkIfPodsDefinitionsAreScaledUpOrDown();
        checkIfPodsDefinitionsWereDeleted();
    }

    private void checkIfPodsDefinitionsWereDeleted() {
        List<PodInst> podInstList = podInstDAO.findPodInstsByPodDefIsNull();
        for (PodInst podInst : podInstList) {
            podService.deletePodInst(podInst.getPodId(), 1000);
        }
    }

    private void checkIfPodsDefinitionsAreScaledUpOrDown() {
        List<PodDef> podDefList = podDefDAO.findAll();
        for (PodDef podDef : podDefList) {
            int podDiff = podDef.getPodInstances().size() - podDef.getCount();
            while (podDiff != 0) {
                if (podDiff < 0) {
                    podService.createPodInst(podDef);
                    podDiff++;
                }
                if (podDiff > 0) {
                    podService.deletePodInst(podDef.getPodInstances().get(podDiff - 1).getPodId(), 1000);
                    podDiff--;
                }
            }
        }
    }
}
