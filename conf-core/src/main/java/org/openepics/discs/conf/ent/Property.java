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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Property.findAll", query = "SELECT p FROM Property p"),
    @NamedQuery(name = "Property.findByPropertyId", query = "SELECT p FROM Property p WHERE p.propertyId = :propertyId"),
    @NamedQuery(name = "Property.findByName", query = "SELECT p FROM Property p WHERE p.name = :name"),
    @NamedQuery(name = "Property.findByDescription", query = "SELECT p FROM Property p WHERE p.description = :description"),
    @NamedQuery(name = "Property.findByAssociation", query = "SELECT p FROM Property p WHERE p.association = :association"),
    @NamedQuery(name = "Property.findByModifiedAt", query = "SELECT p FROM Property p WHERE p.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "Property.findByModifiedBy", query = "SELECT p FROM Property p WHERE p.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "Property.findByVersion", query = "SELECT p FROM Property p WHERE p.version = :version")})
public class Property implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "property_id")
    private Integer propertyId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "association")
    private PropertyAssociation association;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "property")
    private List<ComptypeProperty> comptypePropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "property")
    private List<DeviceProperty> devicePropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "property")
    private List<AlignmentProperty> alignmentPropertyList;
    @JoinColumn(name = "data_type", referencedColumnName = "data_type_id")
    @ManyToOne(optional = false)
    private DataType dataType;
    @JoinColumn(name = "unit", referencedColumnName = "unit_id")
    @ManyToOne
    private Unit unit;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "property")
    private List<SlotProperty> slotPropertyList;

    protected Property() {
    }

    public Property(String name, String description, PropertyAssociation association, String modifiedBy) {
        this.name = name;
        this.description = description;
        this.association = association;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public Integer getPropertyId() {
        return propertyId;
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

    public PropertyAssociation getAssociation() {
        return association;
    }

    public void setAssociation(PropertyAssociation association) {
        this.association = association;
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
    public List<DeviceProperty> getDevicePropertyList() {
        return devicePropertyList;
    }

    public void setDevicePropertyList(List<DeviceProperty> devicePropertyList) {
        this.devicePropertyList = devicePropertyList;
    }

    @XmlTransient
    public List<AlignmentProperty> getAlignmentPropertyList() {
        return alignmentPropertyList;
    }

    public void setAlignmentPropertyList(List<AlignmentProperty> alignmentPropertyList) {
        this.alignmentPropertyList = alignmentPropertyList;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @XmlTransient
    public List<SlotProperty> getSlotPropertyList() {
        return slotPropertyList;
    }

    public void setSlotPropertyList(List<SlotProperty> slotPropertyList) {
        this.slotPropertyList = slotPropertyList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (propertyId != null ? propertyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Property)) {
            return false;
        }
        Property other = (Property) object;
        if ((this.propertyId == null && other.propertyId != null) || (this.propertyId != null && !this.propertyId.equals(other.propertyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.Property[ propertyId=" + propertyId + " ]";
    }

}
