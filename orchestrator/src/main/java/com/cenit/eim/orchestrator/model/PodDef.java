package com.cenit.eim.orchestrator.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class PodDef extends BusinessEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String namespace;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private long created_at;

    @OneToMany(mappedBy = "podDef")
    private List<PodInst> podInstances;

    @OneToMany(mappedBy = "podDef")
    private List<ContainerDef> containerDefs;

    public PodDef() {
    }

    public PodDef(String Name, String namespace, Integer count, long created_at) {
        this.name = Name;
        this.namespace = namespace;
        this.count = count;
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String podName) {
        this.name = podName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String podNamespace) {
        this.namespace = podNamespace;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<PodInst> getPodInstances() {
        return podInstances;
    }

    public List<ContainerDef> getContainers() {
        return containerDefs;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }
}
