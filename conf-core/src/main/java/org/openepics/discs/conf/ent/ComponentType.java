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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
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
    @NamedQuery(name = "ComponentType.findByName", query = "SELECT c FROM ComponentType c WHERE c.name = :name"),
    @NamedQuery(name = "ComponentType.findByDescription", query = "SELECT c FROM ComponentType c WHERE c.description = :description"),
    @NamedQuery(name = "ComponentType.findByModifiedAt", query = "SELECT c FROM ComponentType c WHERE c.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "ComponentType.findByModifiedBy", query = "SELECT c FROM ComponentType c WHERE c.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "ComponentType.findByVersion", query = "SELECT c FROM ComponentType c WHERE c.version = :version")})
public class ComponentType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_type_id")
    private Integer componentTypeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "name")
    private String name;
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
    @Version
    private Long version;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType")
    private List<ComptypeProperty> comptypePropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType")
    private List<Slot> slotList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "childType")
    private List<ComptypeAsm> comptypeAsmList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentType")
    private List<ComptypeAsm> comptypeAsmList1;
    @OneToMany(mappedBy = "superComponentType")
    private List<ComponentType> componentTypeList;
    @JoinColumn(name = "super_component_type", referencedColumnName = "component_type_id")
    @ManyToOne
    private ComponentType superComponentType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType")
    private List<Device> deviceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType")
    private List<ComptypeArtifact> comptypeArtifactList;

    public ComponentType() {
    }

    public ComponentType(String name, Date modifiedAt, String modifiedBy) {
        this.name = name;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
    }

    public Integer getComponentTypeId() {
        return componentTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    protected Long getVersion() {
        return version;
    }

    protected void setVersion(Long version) {
        this.version = version;
    }

    @XmlTransient
    public List<ComptypeProperty> getComptypePropertyList() {
        return comptypePropertyList;
    }

    public void setComptypePropertyList(List<ComptypeProperty> comptypePropertyList) {
        this.comptypePropertyList = comptypePropertyList;
    }

    @XmlTransient
    public List<Slot> getSlotList() {
        return slotList;
    }

    public void setSlotList(List<Slot> slotList) {
        this.slotList = slotList;
    }

    @XmlTransient
    public List<ComptypeAsm> getComptypeAsmList() {
        return comptypeAsmList;
    }

    public void setComptypeAsmList(List<ComptypeAsm> comptypeAsmList) {
        this.comptypeAsmList = comptypeAsmList;
    }

    @XmlTransient
    public List<ComptypeAsm> getComptypeAsmList1() {
        return comptypeAsmList1;
    }

    public void setComptypeAsmList1(List<ComptypeAsm> comptypeAsmList1) {
        this.comptypeAsmList1 = comptypeAsmList1;
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
    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    @XmlTransient
    public List<ComptypeArtifact> getComptypeArtifactList() {
        return comptypeArtifactList;
    }

    public void setComptypeArtifactList(List<ComptypeArtifact> comptypeArtifactList) {
        this.comptypeArtifactList = comptypeArtifactList;
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
