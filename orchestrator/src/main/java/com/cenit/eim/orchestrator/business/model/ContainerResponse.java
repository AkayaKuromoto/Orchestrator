package com.cenit.eim.orchestrator.business.model;

public class ContainerResponse {

    private String containerId;
    private String containerImage;

    public ContainerResponse() {
    }

    public ContainerResponse(String containerId, String containerImage) {
        this.containerId = containerId;
        this.containerImage = containerImage;
    }

    public String getContainerImage() {
        return containerImage;
    }

    public void setContainerImage(String containerImage) {
        this.containerImage = containerImage;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }
}
