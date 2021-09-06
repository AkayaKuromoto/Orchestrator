package com.cenit.eim.orchestrator.business;

import com.cenit.eim.orchestrator.model.Pod;
import org.openapitools.model.PodCreateRequest;
import org.openapitools.model.ContainerCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PodServiceImpl implements PodService {

    private final CRIService criService;

    @Autowired
    public PodServiceImpl(CRIService criService){
        this.criService = criService;
    }

    @Override
    public List<Pod> getAllPods() {
        return criService.getAllPods();
    }

    @Override
    public Pod getPod(String podId, boolean verbose) {
        return criService.getPod(podId, verbose);
    }

    @Override
    public String createPod(PodCreateRequest podCreateRequest) {
        String uuid = UUID.randomUUID().toString();

        List<ContainerCreateRequest> containerRequests =
                podCreateRequest.getContainer()
                        .stream()
                        .map(
                                request -> new ContainerCreateRequest()
                                        .containerName(request.getContainerName())
                                        .imageId(request.getImageId()))
                        .collect(Collectors.toList());

        Pod pod = criService.createPodWithContainers(podCreateRequest.getPodName(),
                uuid,
                podCreateRequest.getPodNamespace(),
                containerRequests,
                podCreateRequest.getAttempt());

        return pod.getPodId();
    }

    @Override
    public void deletePodById(String podId, long timeout) {
        criService.deletePodById(podId, timeout);
    }

    @Override
    public void deletePodByNamespace(String podNamespace, long timeout) {
        criService.deletePodByNamespace(podNamespace, timeout);
    }

    @Override
    public List<Pod> getPodsById(String... podId) {
        List<String> podIds = new ArrayList<>(Arrays.asList(podId));

        return criService.getAllPods()
                .stream()
                .filter(pod -> podIds.contains(pod.getPodId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pod> getPodsByNamespace(String podNamespace) {
        return criService.getAllPods()
                .stream()
                .filter(pod -> pod.getPodNamespace().equals(podNamespace))
                .collect(Collectors.toList());
    }
}
