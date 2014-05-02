/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "component_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentType.findAll", query = "SELECT c FROM ComponentType c"),
    @NamedQuery(name = "ComponentType.findByComponentTypeId", query = "SELECT c FROM ComponentType c WHERE c.componentTypeId = :componentTypeId"),
    @NamedQuery(name = "ComponentType.findByDescription", query = "SELECT c FROM ComponentType c WHERE c.description = :description"),
    @NamedQuery(name = "ComponentType.findByModifiedAt", query = "SELECT c FROM ComponentType c WHERE c.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "ComponentType.findByModifiedBy", query = "SELECT c FROM ComponentType c WHERE c.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "ComponentType.findByVersion", query = "SELECT c FROM ComponentType c WHERE c.version = :version")})
public class ComponentType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "component_type_id")
    private String componentTypeId;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType1")
    private List<ComponentTypeProperty> componentTypePropertyList;
    @OneToMany(mappedBy = "superComponentType")
    private List<ComponentType> componentTypeList;
    @JoinColumn(name = "super_component_type", referencedColumnName = "component_type_id")
    @ManyToOne
    private ComponentType superComponentType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType")
    private List<CtArtifact> ctArtifactList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType")
    private List<Device> deviceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "childType")
    private List<CompTypeAsm> compTypeAsmList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType")
    private List<CompTypeAsm> compTypeAsmList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType")
    private List<Slot> slotList;

    public ComponentType() {
    }

    public ComponentType(String componentTypeId) {
        this.componentTypeId = componentTypeId;
    }

    public ComponentType(String componentTypeId, Date modifiedAt, String modifiedBy, int version) {
        this.componentTypeId = componentTypeId;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.version = version;
    }

    public String getComponentTypeId() {
        return componentTypeId;
    }

    public void setComponentTypeId(String componentTypeId) {
        this.componentTypeId = componentTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @XmlTransient
    public List<ComponentTypeProperty> getComponentTypePropertyList() {
        return componentTypePropertyList;
    }

    public void setComponentTypePropertyList(List<ComponentTypeProperty> componentTypePropertyList) {
        this.componentTypePropertyList = componentTypePropertyList;
    }

    @XmlTransient
    public List<ComponentType> getComponentTypeList() {
        return componentTypeList;
    }

    public void setComponentTypeList(List<ComponentType> componentTypeList) {
        this.componentTypeList = componentTypeList;
    }

    public ComponentType getSuperComponentType() {
        return superComponentType;
    }

    public void setSuperComponentType(ComponentType superComponentType) {
        this.superComponentType = superComponentType;
    }

    @XmlTransient
    public List<CtArtifact> getCtArtifactList() {
        return ctArtifactList;
    }

    public void setCtArtifactList(List<CtArtifact> ctArtifactList) {
        this.ctArtifactList = ctArtifactList;
    }

    @XmlTransient
    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    @XmlTransient
    public List<CompTypeAsm> getCompTypeAsmList() {
        return compTypeAsmList;
    }

    public void setCompTypeAsmList(List<CompTypeAsm> compTypeAsmList) {
        this.compTypeAsmList = compTypeAsmList;
    }

    @XmlTransient
    public List<CompTypeAsm> getCompTypeAsmList1() {
        return compTypeAsmList1;
    }

    public void setCompTypeAsmList1(List<CompTypeAsm> compTypeAsmList1) {
        this.compTypeAsmList1 = compTypeAsmList1;
    }

    @XmlTransient
    public List<Slot> getSlotList() {
        return slotList;
    }

    public void setSlotList(List<Slot> slotList) {
        this.slotList = slotList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentTypeId != null ? componentTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentType)) {
            return false;
        }
        ComponentType other = (ComponentType) object;
        if ((this.componentTypeId == null && other.componentTypeId != null) || (this.componentTypeId != null && !this.componentTypeId.equals(other.componentTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.ComponentType[ componentTypeId=" + componentTypeId + " ]";
    }
    
}
