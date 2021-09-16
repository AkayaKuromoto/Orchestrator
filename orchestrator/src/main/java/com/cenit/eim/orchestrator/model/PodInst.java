package com.cenit.eim.orchestrator.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class PodInst extends BusinessEntity {

    @Column
    private String podId;

    @Column(nullable = false)
    private PodState state;

    @Column(nullable = false)
    private long created_at;

    @ManyToOne
    private PodDef podDef;

    @OneToMany(mappedBy = "pod")
    private List<ContainerInst> containerInstances;

    public PodInst() {
    }

    public PodInst(String podId, PodState state, Long created_at, PodDef podDef) {
        this.podId = podId;
        this.state = state;
        this.created_at = created_at;
        this.podDef = podDef;
    }

    public String getPodId() {
        return podId;
    }

    public void setPodId(String podId) {
        this.podId = podId;
    }

    public PodState getState() {
        return state;
    }

    public void setState(PodState state) {
        this.state = state;
    }

    public PodDef getPod() {
        return podDef;
    }

    public void setPod(PodDef podDef) {
        this.podDef = podDef;
    }

    public List<ContainerInst> getContainerInstances() {
        return containerInstances;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }
}
