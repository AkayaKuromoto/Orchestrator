package com.cenit.eim.orchestrator.model;

import javax.persistence.*;

@MappedSuperclass
public abstract class BusinessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    public BusinessEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        if (getId() == null) {
            return super.hashCode();
        } else {
            return getId().hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BusinessEntity)) {
            return false;
        }
        // ist es überhaupt der gleiche Typ (beide Richtungen vergleichen wegen potentieller dynamischer Subklassen)
        if (!isAssignableFrom(this.getClass(), obj.getClass()) && !isAssignableFrom(obj.getClass(), this.getClass())) {
            return false;
        }
        BusinessEntity other = (BusinessEntity) obj;
        if (getId() == null && other.getId() == null) {
            // beide scheinen neu -> Objektvergleich
            return super.equals(other);
        }
        if (getId() == null || other.getId() == null) {
            // eine der beiden Entitäten ist neu
            return false;
        }
        // sonst Vergleich der Ids
        return getId().equals(other.getId());
    }

    private boolean isAssignableFrom(Class<?> classA, Class<?> classB) {
        Class<?> superClass = classB;
        do {
            if (superClass == classA) {
                return true;
            }
        } while ((superClass = superClass.getSuperclass()) != null);
        return false;
    }
}
