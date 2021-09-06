package com.cenit.eim.orchestrator.business;

import com.cenit.eim.orchestrator.model.Pod;
import org.openapitools.model.PodCreateRequest;

import java.util.List;

public interface PodService {

    List<Pod> getAllPods();

    Pod getPod(String podId, boolean verbose);

    String createPod(PodCreateRequest podCreateRequest);

    void deletePodById(String podId, long timeout);

    void deletePodByNamespace(String podNamespace, long timeout);

    List<Pod> getPodsById(String... podId);

    List<Pod> getPodsByNamespace(String podNamespace);
}
