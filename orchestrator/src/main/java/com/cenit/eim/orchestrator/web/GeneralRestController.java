package com.cenit.eim.orchestrator.web;

import com.cenit.eim.orchestrator.api.model.ImagePullRequestDto;
import com.cenit.eim.orchestrator.api.model.ImageStatusResponseDto;
import com.cenit.eim.orchestrator.api.model.ImageInformationResponseDto;
import com.cenit.eim.orchestrator.api.model.PodDefinitionDetailsResponseDto;
import com.cenit.eim.orchestrator.api.model.PodDefinitionCreateRequestDto;
import com.cenit.eim.orchestrator.api.model.PodDefinitionResponseDto;
import com.cenit.eim.orchestrator.business.PodService;
import com.cenit.eim.orchestrator.business.ImageService;
import com.cenit.eim.orchestrator.business.exception.ImageNotFoundException;
import com.cenit.eim.orchestrator.model.PodDef;
import com.cenit.eim.orchestrator.api.ImagesApi;
import com.cenit.eim.orchestrator.api.PodsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
/*make it per method*/
@Transactional
public class GeneralRestController implements ImagesApi, PodsApi {


    private final PodService podService;
    private final ImageService imageService;

    @Autowired
    public GeneralRestController(PodService podService, ImageService imageService) {
        this.podService = podService;
        this.imageService = imageService;
    }

    @Override
    public ResponseEntity<Void> createPodDefinition(PodDefinitionCreateRequestDto podDefinitionCreateRequestDto) {
        podService.createPodDefinition(podDefinitionCreateRequestDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<PodDefinitionResponseDto>> getPodDefinitionList(List<String> podDefinitionNames, String podDefinitionNamespace) {
        if(podDefinitionNames != null && podDefinitionNamespace != null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(podDefinitionNames == null){
            podDefinitionNames = new ArrayList<>();
        }
        List<PodDef> podDefList = new ArrayList<>();

        if(podDefinitionNames.isEmpty() && podDefinitionNamespace == null){
            podDefList = podService.getAllPodDefinition();
        }else
        if(podDefinitionNames.isEmpty()){
            podDefList = podService.getPodDefinitionsByNames(podDefinitionNames);
        }else
        if(podDefinitionNamespace != null){
            podDefList = podService.getPodDefinitionsByNamespace(podDefinitionNamespace);
        }

        List<PodDefinitionResponseDto> result = ResponseMapper.toResponse(podDefList);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deletePodDefinition(List<String> podDefinitionNames, String podDefinitionNamespace) {
        if((podDefinitionNames == null && podDefinitionNamespace == null) ||
                (podDefinitionNames != null && podDefinitionNamespace != null)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(podDefinitionNamespace != null){
            if(podDefinitionNamespace.isEmpty()){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            podService.deletePodDefinitionByNamespace(podDefinitionNamespace);
        }else if(podDefinitionNames != null){
            if(podDefinitionNames.isEmpty()){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            podService.deletePodDefinitionByNames(podDefinitionNames);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PodDefinitionDetailsResponseDto> getPodDefinitionByName(String podDefinitionName) {
        PodDef pod = podService.getPodDefinitionByName(podDefinitionName);

        PodDefinitionDetailsResponseDto response = ResponseMapper.toResponseDetail(pod);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ImageStatusResponseDto> getImageStatus(String imageId, Boolean verbose) {
        if((imageId == null || imageId.isBlank() || imageId.isEmpty())){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        ImageStatusResponseDto response = ResponseMapper.toResponse(
                imageService.getImageStatus(imageId, verbose)
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<String> pullImage(ImagePullRequestDto imagePullRequest) {
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
    public Optional<NativeWebRequest> getRequest() {
        return ImagesApi.super.getRequest();
    }

    @Override
    public ResponseEntity<List<ImageInformationResponseDto>> getImageList() {
        List<ImageInformationResponseDto> response =
                ResponseMapper.toResponse(
                        imageService.getImages()
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
