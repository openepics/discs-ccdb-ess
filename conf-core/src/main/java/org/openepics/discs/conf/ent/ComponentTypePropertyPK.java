/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author vuppala
 */
@Embeddable
public class ComponentTypePropertyPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "component_type")
    private String componentType;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "property")
    private String property;

    public ComponentTypePropertyPK() {
    }

    public ComponentTypePropertyPK(String componentType, String property) {
        this.componentType = componentType;
        this.property = property;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentType != null ? componentType.hashCode() : 0);
        hash += (property != null ? property.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentTypePropertyPK)) {
            return false;
        }
        ComponentTypePropertyPK other = (ComponentTypePropertyPK) object;
        if ((this.componentType == null && other.componentType != null) || (this.componentType != null && !this.componentType.equals(other.componentType))) {
            return false;
        }
        if ((this.property == null && other.property != null) || (this.property != null && !this.property.equals(other.property))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.ComponentTypePropertyPK[ componentType=" + componentType + ", property=" + property + " ]";
    }
    
}
