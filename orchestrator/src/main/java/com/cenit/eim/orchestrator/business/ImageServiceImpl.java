package com.cenit.eim.orchestrator.business;

import com.cenit.eim.orchestrator.business.exception.ImageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import runtime.v1alpha2.ImageStatusResponse;
import runtime.v1alpha2.ListImagesResponse;

import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService{

    private final CRIService criService;

    @Autowired
    public ImageServiceImpl(CRIService criService){
        this.criService = criService;
    }

    @Override
    public String removeImage(String imageId, Map<String, String> imageAnnotations) {
        return criService.removeImage(imageId, imageAnnotations);
    }

    @Override
    public String pullImage(String imageId, Map<String, String> imageAnnotations) throws ImageNotFoundException {
        return criService.pullImage(imageId, imageAnnotations);
    }

    @Override
    public ListImagesResponse getImages() {
        return criService.listImages();
    }

    @Override
    public ImageStatusResponse getImageStatus(String imageId, Map<String, String> imageAnnotations, boolean verbose) {
        return criService.getImageStatus(imageId, imageAnnotations, verbose);
    }
}
