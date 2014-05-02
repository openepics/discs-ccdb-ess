/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "component_type_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentTypeProperty.findAll", query = "SELECT c FROM ComponentTypeProperty c"),
    @NamedQuery(name = "ComponentTypeProperty.findByComponentType", query = "SELECT c FROM ComponentTypeProperty c WHERE c.componentTypePropertyPK.componentType = :componentType"),
    @NamedQuery(name = "ComponentTypeProperty.findByProperty", query = "SELECT c FROM ComponentTypeProperty c WHERE c.componentTypePropertyPK.property = :property"),
    @NamedQuery(name = "ComponentTypeProperty.findByType", query = "SELECT c FROM ComponentTypeProperty c WHERE c.type = :type"),
    @NamedQuery(name = "ComponentTypeProperty.findByModifiedAt", query = "SELECT c FROM ComponentTypeProperty c WHERE c.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "ComponentTypeProperty.findByModifiedBy", query = "SELECT c FROM ComponentTypeProperty c WHERE c.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "ComponentTypeProperty.findByVersion", query = "SELECT c FROM ComponentTypeProperty c WHERE c.version = :version")})
public class ComponentTypeProperty implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ComponentTypePropertyPK componentTypePropertyPK;
    @Size(max = 4)
    @Column(name = "type")
    private String type;
    @Lob
    @Size(max = 65535)
    @Column(name = "value")
    private String value;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modified_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "modified_by")
    private String modifiedBy;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @JoinColumn(name = "property", referencedColumnName = "property_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Property property1;
    @JoinColumn(name = "component_type", referencedColumnName = "component_type_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ComponentType componentType1;

    public ComponentTypeProperty() {
    }

    public ComponentTypeProperty(ComponentTypePropertyPK componentTypePropertyPK) {
        this.componentTypePropertyPK = componentTypePropertyPK;
    }

    public ComponentTypeProperty(ComponentTypePropertyPK componentTypePropertyPK, Date modifiedAt, String modifiedBy, int version) {
        this.componentTypePropertyPK = componentTypePropertyPK;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.version = version;
    }

    public ComponentTypeProperty(String componentType, String property) {
        this.componentTypePropertyPK = new ComponentTypePropertyPK(componentType, property);
    }

    public ComponentTypePropertyPK getComponentTypePropertyPK() {
        return componentTypePropertyPK;
    }

    public void setComponentTypePropertyPK(ComponentTypePropertyPK componentTypePropertyPK) {
        this.componentTypePropertyPK = componentTypePropertyPK;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Property getProperty1() {
        return property1;
    }

    public void setProperty1(Property property1) {
        this.property1 = property1;
    }

    public ComponentType getComponentType1() {
        return componentType1;
    }

    public void setComponentType1(ComponentType componentType1) {
        this.componentType1 = componentType1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentTypePropertyPK != null ? componentTypePropertyPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentTypeProperty)) {
            return false;
        }
        ComponentTypeProperty other = (ComponentTypeProperty) object;
        if ((this.componentTypePropertyPK == null && other.componentTypePropertyPK != null) || (this.componentTypePropertyPK != null && !this.componentTypePropertyPK.equals(other.componentTypePropertyPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.ComponentTypeProperty[ componentTypePropertyPK=" + componentTypePropertyPK + " ]";
    }
    
}
