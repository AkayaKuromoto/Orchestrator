package com.cenit.eim.orchestrator.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class ContainerDef extends BusinessEntity {

    @Column(nullable = false, updatable = false)
    private String containerName;

    @Column(nullable = false, updatable = false)
    private String containerNamespace;

    @Column(nullable = false, updatable = false)
    private String image;

    @OneToMany(mappedBy = "containerDef")
    private List<ContainerInst> containerInstances;

    @ManyToOne(optional = false)
    private PodDef podDef;

    public ContainerDef() {
    }

    public ContainerDef(String containerName, String containerNamespace, String image, PodDef podDef) {
        this.containerName = containerName;
        this.containerNamespace = containerNamespace;
        this.image = image;
        this.podDef = podDef;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getContainerNamespace() {
        return containerNamespace;
    }

    public void setContainerNamespace(String containerNamespace) {
        this.containerNamespace = containerNamespace;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<ContainerInst> getContainerInstances() {
        return containerInstances;
    }

    public PodDef getPod() {
        return podDef;
    }

    public void setPod(PodDef podDef) {
        this.podDef = podDef;
    }
}
