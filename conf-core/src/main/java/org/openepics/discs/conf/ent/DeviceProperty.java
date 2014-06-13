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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "device_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DeviceProperty.findAll", query = "SELECT d FROM DeviceProperty d"),
    @NamedQuery(name = "DeviceProperty.findByDevPropId", query = "SELECT d FROM DeviceProperty d WHERE d.devPropId = :devPropId"),
    @NamedQuery(name = "DeviceProperty.findByInRepository", query = "SELECT d FROM DeviceProperty d WHERE d.inRepository = :inRepository"),
    @NamedQuery(name = "DeviceProperty.findByModifiedAt", query = "SELECT d FROM DeviceProperty d WHERE d.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "DeviceProperty.findByModifiedBy", query = "SELECT d FROM DeviceProperty d WHERE d.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "DeviceProperty.findByVersion", query = "SELECT d FROM DeviceProperty d WHERE d.version = :version")})
public class DeviceProperty implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "dev_prop_id")
    private Integer devPropId;
    @Column(name = "prop_value", columnDefinition="TEXT")
    private String propValue;
    @Basic(optional = false)
    @NotNull
    @Column(name = "in_repository")
    private boolean inRepository;
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
    @JoinColumn(name = "property", referencedColumnName = "property_id")
    @ManyToOne(optional = false)
    private Property property;
    @JoinColumn(name = "unit", referencedColumnName = "unit_id")
    @ManyToOne
    private Unit unit;
    @JoinColumn(name = "device", referencedColumnName = "device_id")
    @ManyToOne(optional = false)
    private Device device;

    public DeviceProperty() {
    }

    public DeviceProperty(Integer devPropId) {
        this.devPropId = devPropId;
    }

    public DeviceProperty(Integer devPropId, boolean inRepository, Date modifiedAt, String modifiedBy, int version) {
        this.devPropId = devPropId;
        this.inRepository = inRepository;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.version = version;
    }

    public Integer getDevPropId() {
        return devPropId;
    }

    public void setDevPropId(Integer devPropId) {
        this.devPropId = devPropId;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    public boolean getInRepository() {
        return inRepository;
    }

    public void setInRepository(boolean inRepository) {
        this.inRepository = inRepository;
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

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (devPropId != null ? devPropId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DeviceProperty)) {
            return false;
        }
        DeviceProperty other = (DeviceProperty) object;
        if ((this.devPropId == null && other.devPropId != null) || (this.devPropId != null && !this.devPropId.equals(other.devPropId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.DeviceProperty[ devPropId=" + devPropId + " ]";
    }
    
}
