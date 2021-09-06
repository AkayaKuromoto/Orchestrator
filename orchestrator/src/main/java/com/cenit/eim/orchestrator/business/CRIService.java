package com.cenit.eim.orchestrator.business;

import com.cenit.eim.orchestrator.business.exception.ImageNotFoundException;

import com.cenit.eim.orchestrator.model.Pod;
import org.openapitools.model.ContainerCreateRequest;
import runtime.v1alpha2.*;

import java.util.List;
import java.util.Map;

public interface CRIService {

    List<Pod> getAllPods();

    Pod getPod(String podId, boolean verbose);

    Pod createPodWithContainers(String podName, String podUid, String podNamespace, List<ContainerCreateRequest> containerRequests, int attempt);

    void deletePodById(String podId, long timeout);

    void deletePodByNamespace(String namespace, long timeout);


    ListImagesResponse listImages();

    ImageStatusResponse getImageStatus(String imageId, Map<String, String> imageAnnotations, boolean verbose);

    String pullImage(String imageId, Map<String, String> imageAnnotations) throws ImageNotFoundException;

    String removeImage(String imageId, Map<String, String> imageAnnotations);
}
