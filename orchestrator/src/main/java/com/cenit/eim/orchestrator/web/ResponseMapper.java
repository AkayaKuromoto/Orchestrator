package com.cenit.eim.orchestrator.web;

import com.cenit.eim.orchestrator.model.ContainerDef;
import com.cenit.eim.orchestrator.api.model.PodInstanceResponseDto;
import com.cenit.eim.orchestrator.model.PodDef;
import com.cenit.eim.orchestrator.api.model.ContainerDefinitionResponseDto;
import com.cenit.eim.orchestrator.api.model.PodDefinitionDetailsResponseDto;
import com.cenit.eim.orchestrator.api.model.PodDefinitionResponseDto;
import com.cenit.eim.orchestrator.api.model.ImageInformationResponseDto;
import com.cenit.eim.orchestrator.api.model.ImageStatusResponseDto;
import com.cenit.eim.orchestrator.model.PodInst;
import runtime.v1alpha2.Image;
import runtime.v1alpha2.ImageStatusResponse;
import runtime.v1alpha2.ListImagesResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResponseMapper {

    public static PodDefinitionResponseDto toResponse(PodDef podDef){
        return new PodDefinitionResponseDto()
                .name(podDef.getName())
                .namespace(podDef.getNamespace())
                .desiredCount(podDef.getCount())
                .actualCount(podDef.getPodInstances().size());
    }

    public static List<PodDefinitionResponseDto> toResponse(List<PodDef> podDefInstList) {
        return podDefInstList
                .stream()
                .map(ResponseMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static ImageInformationResponseDto toResponse(Image image){
        return new ImageInformationResponseDto()
                .id(image.getId())
                .repoTags(new ArrayList<>(image.getRepoTagsList()))
                .repoDigests(new ArrayList<>(image.getRepoDigestsList()))
                .size(image.getSize())
                .imageName(image.getSpec().getImage());
    }

    public static List<ImageInformationResponseDto> toResponse(ListImagesResponse imagesResponse){
        return imagesResponse.getImagesList()
                .stream()
                .map(ResponseMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static ImageStatusResponseDto toResponse(ImageStatusResponse imageStatusResponse){
        ImageInformationResponseDto imageInformationResponse =
                ResponseMapper.toResponse(imageStatusResponse.getImage());

        return new ImageStatusResponseDto()
                .image(imageInformationResponse)
                .verboseInformation(imageStatusResponse.getInfoMap());
    }

    public static PodDefinitionDetailsResponseDto toResponseDetail(PodDef pod) {


        return new PodDefinitionDetailsResponseDto()
                .name(pod.getName())
                .namespace(pod.getNamespace())
                .desiredCount(pod.getCount())
                .actualCount(pod.getPodInstances().size())
                .createdAt(pod.getCreated_at())
                .container(pod.getContainers().stream().map(ResponseMapper::toResponse).collect(Collectors.toList()))
                .instances(pod.getPodInstances().stream().map(ResponseMapper::toResponse).collect(Collectors.toList()));
    }

    public static PodInstanceResponseDto toResponse(PodInst podInst){
        return new PodInstanceResponseDto()
                .id(podInst.getPodId())
                .state(podInst.getState().toString())
                .createdAt(podInst.getCreated_at());
    }

    public static ContainerDefinitionResponseDto toResponse(ContainerDef containerDef){
        return new ContainerDefinitionResponseDto()
                .name(containerDef.getContainerName())
                .image(containerDef.getImage());
    }
}
