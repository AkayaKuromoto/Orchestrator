package com.cenit.eim.orchestrator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ContainerInst extends BusinessEntity {

    @Column(unique = true, updatable = false)
    private String containerId;

    @Column(nullable = false)
    private ContainerState state;

    @ManyToOne
    private ContainerDef containerDef;

    @ManyToOne
    private PodInst pod;

    public ContainerInst() {
    }

    public ContainerInst(String containerId, ContainerState state, ContainerDef containerDef, PodInst pod) {
        this.containerId = containerId;
        this.state = state;
        this.containerDef = containerDef;
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

    public ContainerDef getContainer() {
        return containerDef;
    }

    public void setContainer(ContainerDef containerDef) {
        this.containerDef = containerDef;
    }

    public PodInst getPod() {
        return pod;
    }

    public void setPod(PodInst pod) {
        this.pod = pod;
    }
}
