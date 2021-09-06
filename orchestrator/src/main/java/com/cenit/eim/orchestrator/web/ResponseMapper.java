package com.cenit.eim.orchestrator.web;

import com.cenit.eim.orchestrator.model.Pod;
import org.openapitools.model.ImageInformationResponse;
import org.openapitools.model.ImageStatusResponse;
import org.openapitools.model.PodResponse;
import runtime.v1alpha2.Image;
import runtime.v1alpha2.ListImagesResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResponseMapper {

    public static PodResponse toResponse(Pod pod){
        return new PodResponse()
                .podId(pod.getPodId())
                .podName(pod.getPodName())
                .podNamespace(pod.getPodNamespace())
                .state(pod.getState().toString());
    }

    public static List<PodResponse> toResponse(List<Pod> pods) {
        return pods
                .stream()
                .map(ResponseMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static ImageInformationResponse toResponse(Image image){
        return new ImageInformationResponse()
                .id(image.getId())
                .repoTags(new ArrayList<>(image.getRepoTagsList()))
                .repoDigests(new ArrayList<>(image.getRepoDigestsList()))
                .size(image.getSize())
                .uid(image.getUid().getValue())
                .username(image.getUsername())
                .imageName(image.getSpec().getImage())
                .annotations(image.getSpec().getAnnotationsMap());
    }

    public static List<ImageInformationResponse> toResponse(ListImagesResponse imagesResponse){
        return imagesResponse.getImagesList()
                .stream()
                .map(ResponseMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static ImageStatusResponse toResponse(runtime.v1alpha2.ImageStatusResponse imageStatusResponse){
        ImageInformationResponse imageInformationResponse =
                ResponseMapper.toResponse(imageStatusResponse.getImage());

        return new ImageStatusResponse()
                .image(imageInformationResponse)
                .verboseInformation(imageStatusResponse.getInfoMap());
    }
}
