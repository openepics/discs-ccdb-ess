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
@Table(name = "device_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DeviceProperty.findAll", query = "SELECT d FROM DeviceProperty d"),
    @NamedQuery(name = "DeviceProperty.findByDevice", query = "SELECT d FROM DeviceProperty d WHERE d.devicePropertyPK.device = :device"),
    @NamedQuery(name = "DeviceProperty.findByProperty", query = "SELECT d FROM DeviceProperty d WHERE d.devicePropertyPK.property = :property"),
    @NamedQuery(name = "DeviceProperty.findByModifiedAt", query = "SELECT d FROM DeviceProperty d WHERE d.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "DeviceProperty.findByModifiedBy", query = "SELECT d FROM DeviceProperty d WHERE d.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "DeviceProperty.findByVersion", query = "SELECT d FROM DeviceProperty d WHERE d.version = :version")})
public class DeviceProperty implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DevicePropertyPK devicePropertyPK;
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
    @JoinColumn(name = "device", referencedColumnName = "device_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Device device1;

    public DeviceProperty() {
    }

    public DeviceProperty(DevicePropertyPK devicePropertyPK) {
        this.devicePropertyPK = devicePropertyPK;
    }

    public DeviceProperty(DevicePropertyPK devicePropertyPK, Date modifiedAt, String modifiedBy, int version) {
        this.devicePropertyPK = devicePropertyPK;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.version = version;
    }

    public DeviceProperty(int device, String property) {
        this.devicePropertyPK = new DevicePropertyPK(device, property);
    }

    public DevicePropertyPK getDevicePropertyPK() {
        return devicePropertyPK;
    }

    public void setDevicePropertyPK(DevicePropertyPK devicePropertyPK) {
        this.devicePropertyPK = devicePropertyPK;
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

    public Device getDevice1() {
        return device1;
    }

    public void setDevice1(Device device1) {
        this.device1 = device1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (devicePropertyPK != null ? devicePropertyPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DeviceProperty)) {
            return false;
        }
        DeviceProperty other = (DeviceProperty) object;
        if ((this.devicePropertyPK == null && other.devicePropertyPK != null) || (this.devicePropertyPK != null && !this.devicePropertyPK.equals(other.devicePropertyPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.DeviceProperty[ devicePropertyPK=" + devicePropertyPK + " ]";
    }
    
}
