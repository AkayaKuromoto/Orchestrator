package com.cenit.eim.orchestrator.model;

import java.util.List;

public class Pod{

    private String podId;
    private String podName;
    private String podNamespace;
    private PodState state;
    private List<Container> containers;

    public Pod() {
    }

    public Pod(String podId, String podName, String podNamespace, PodState state) {
        this.podId = podId;
        this.podName = podName;
        this.podNamespace = podNamespace;
        this.state = state;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public void setPodNamespace(String podNamespace) {
        this.podNamespace = podNamespace;
    }

    public PodState getState() {
        return state;
    }

    public void setState(PodState state) {
        this.state = state;
    }

    public String getPodNamespace() {
        return podNamespace;
    }

    public void setContainers(List<Container> containers) {
        this.containers = containers;
    }

    public String getPodName() {
        return podName;
    }

    public String getPodId() {
        return podId;
    }

    public void setPodId(String podId) {
        this.podId = podId;
    }

    public List<Container> getContainers() {
        return containers;
    }
}
