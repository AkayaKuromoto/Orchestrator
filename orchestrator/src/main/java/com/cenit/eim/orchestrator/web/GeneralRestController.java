package com.cenit.eim.orchestrator.web;

import com.cenit.eim.orchestrator.business.PodService;
import com.cenit.eim.orchestrator.business.ImageService;
import com.cenit.eim.orchestrator.business.exception.ImageNotFoundException;
import com.cenit.eim.orchestrator.model.Pod;
import org.openapitools.model.ImageInformationResponse;
import org.openapitools.model.ImagePullRequest;
import org.openapitools.model.ImageStatusResponse;
import org.openapitools.model.PodCreateRequest;
import org.openapitools.model.PodResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.openapitools.api.ImageApi;
import org.openapitools.api.ImagesApi;
import org.openapitools.api.PodsApi;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class GeneralRestController implements ImageApi, ImagesApi, PodsApi {


    private final PodService podService;
    private final ImageService imageService;

    @Autowired
    public GeneralRestController(PodService podService, ImageService imageService) {
        this.podService = podService;
        this.imageService = imageService;
    }

    @Override
    public ResponseEntity<PodResponse> createPod(PodCreateRequest podCreateRequest) {
        String podId = podService.createPod(podCreateRequest);
        Pod pod = podService.getPod(podId, false);

        PodResponse result = ResponseMapper.toResponse(pod);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<PodResponse>> getPodList(List<String> podIds, String podNamespace) {
        if(podIds != null && podNamespace != null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Pod> pods = new ArrayList<>();
        if(podIds == null && podNamespace == null){
            pods = podService.getAllPods();
        }
        if(podIds != null){
            pods = podService.getPodsById(podIds.toArray(String[]::new));
        }
        if(podNamespace != null){
            pods = podService.getPodsByNamespace(podNamespace);
        }

        List<PodResponse> result = ResponseMapper.toResponse(pods);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deletePod(String podId, String podNamespace) {
        if((podId == null && podNamespace == null) || (podId != null && podNamespace != null)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(podId != null){
            podService.deletePodById(podId, 1000);
        }
        if(podNamespace != null){
            podService.deletePodByNamespace(podNamespace, 1000);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return ImageApi.super.getRequest();
    }

    @Override
    public ResponseEntity<ImageStatusResponse> getImageStatus(String id, Map<String, String> annotations, Boolean verbose) {
        if((id == null || id.isBlank() || id.isEmpty()) && (annotations == null || annotations.isEmpty())){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        ImageStatusResponse response = ResponseMapper.toResponse(
                imageService.getImageStatus(id, annotations, verbose)
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> pullImage(ImagePullRequest imagePullRequest) {
        String response;
        try {
            response = imageService.pullImage(imagePullRequest.getId(), imagePullRequest.getAnnotations());
        } catch (ImageNotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> removeImage(String id, Map<String, String> annotations) {
        String result = imageService.removeImage(id, annotations);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ImageInformationResponse>> getImageList() {
        List<ImageInformationResponse> response =
                ResponseMapper.toResponse(
                        imageService.getImages()
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
