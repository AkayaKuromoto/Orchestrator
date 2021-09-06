package com.cenit.eim.orchestrator.model;

public class Container extends BusinessEntity{

    private String containerId;
    private ContainerState state;
    private String image;
    private Pod pod;

    public Container(){

    }

    public Container(String containerId, ContainerState state, String image, Pod pod) {
        this.containerId = containerId;
        this.state = state;
        this.image = image;
        this.pod = pod;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public ContainerState getState() {
        return state;
    }

    public void setState(ContainerState state) {
        this.state = state;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Pod getPod() {
        return pod;
    }

    public void setPod(Pod pod) {
        this.pod = pod;
    }
}
