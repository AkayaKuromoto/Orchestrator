package com.cenit.eim.orchestrator.business;

import com.cenit.eim.orchestrator.business.exception.ImageNotFoundException;
import runtime.v1alpha2.ImageStatusResponse;
import runtime.v1alpha2.ListImagesResponse;

import java.util.List;
import java.util.Map;

public interface ImageService {

    void removeImage(String imageId);

    void removeImages(List<String> imageIds);

    String pullImage(String imageId, Map<String, String> imageAnnotations) throws ImageNotFoundException;

    ListImagesResponse getImages();

    ImageStatusResponse getImageStatus(String imageId, boolean verbose);
}
