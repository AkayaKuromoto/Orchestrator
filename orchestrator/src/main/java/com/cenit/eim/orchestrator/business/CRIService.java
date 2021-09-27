package com.cenit.eim.orchestrator.business;

import com.cenit.eim.orchestrator.business.exception.ImageNotFoundException;

import com.cenit.eim.orchestrator.model.ContainerDef;
import com.cenit.eim.orchestrator.model.PodDef;
import runtime.v1alpha2.*;

import java.util.Map;

public interface CRIService {

    ListPodSandboxResponse getAllPods();

    RunPodSandboxResponse createPod(PodDef podDef, String uuid, int attempt);

    Map<ContainerDef, CreateContainerResponse> createContainersInPod(PodDef podDef, String podId, String uuid, int attempt);

    void deletePodById(String podId, long timeout);

    ListImagesResponse listImages();

    ImageStatusResponse getImageStatus(String imageId, boolean verbose);

    String pullImage(String imageId, Map<String, String> imageAnnotations) throws ImageNotFoundException;

    String removeImage(String imageId);
}
