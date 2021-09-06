package com.cenit.eim.orchestrator.business.model;

import java.util.List;

public class PodResponse {

    private String podId;
    private String podName;
    private String podNamespace;
    private List<ContainerResponse> container;

    public PodResponse() {
    }

    public PodResponse(String podId, String podName, String podNamespace, List<ContainerResponse> container) {
        this.podId = podId;
        this.podName = podName;
        this.podNamespace = podNamespace;
        this.container = container;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getPodNamespace() {
        return podNamespace;
    }

    public void setPodNamespace(String podNamespace) {
        this.podNamespace = podNamespace;
    }

    public String getPodId() {
        return podId;
    }

    public void setPodId(String podId) {
        this.podId = podId;
    }

    public List<ContainerResponse> getContainer() {
        return container;
    }

    public void setContainer(List<ContainerResponse> container) {
        this.container = container;
    }
}
