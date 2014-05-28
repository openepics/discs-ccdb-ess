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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "device")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Device.findAll", query = "SELECT d FROM Device d"),
    @NamedQuery(name = "Device.findByDeviceId", query = "SELECT d FROM Device d WHERE d.deviceId = :deviceId"),
    @NamedQuery(name = "Device.findBySerialNumber", query = "SELECT d FROM Device d WHERE d.serialNumber = :serialNumber"),
    @NamedQuery(name = "Device.findByDescription", query = "SELECT d FROM Device d WHERE d.description = :description"),
    @NamedQuery(name = "Device.findByStatus", query = "SELECT d FROM Device d WHERE d.status = :status"),
    @NamedQuery(name = "Device.findByManufacturer", query = "SELECT d FROM Device d WHERE d.manufacturer = :manufacturer"),
    @NamedQuery(name = "Device.findByManufModel", query = "SELECT d FROM Device d WHERE d.manufModel = :manufModel"),
    @NamedQuery(name = "Device.findByManufSerialNumber", query = "SELECT d FROM Device d WHERE d.manufSerialNumber = :manufSerialNumber"),
    @NamedQuery(name = "Device.findByLocation", query = "SELECT d FROM Device d WHERE d.location = :location"),
    @NamedQuery(name = "Device.findByPurchaseOrder", query = "SELECT d FROM Device d WHERE d.purchaseOrder = :purchaseOrder"),
    @NamedQuery(name = "Device.findByAsmPosition", query = "SELECT d FROM Device d WHERE d.asmPosition = :asmPosition"),
    @NamedQuery(name = "Device.findByAsmDescription", query = "SELECT d FROM Device d WHERE d.asmDescription = :asmDescription"),
    @NamedQuery(name = "Device.findByModifiedAt", query = "SELECT d FROM Device d WHERE d.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "Device.findByModifiedBy", query = "SELECT d FROM Device d WHERE d.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "Device.findByVersion", query = "SELECT d FROM Device d WHERE d.version = :version")})
public class Device implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "device_id")
    private Integer deviceId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "serial_number")
    private String serialNumber;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @Column(name = "status")
    private Character status;
    @Size(max = 64)
    @Column(name = "manufacturer")
    private String manufacturer;
    @Size(max = 64)
    @Column(name = "manuf_model")
    private String manufModel;
    @Size(max = 64)
    @Column(name = "manuf_serial_number")
    private String manufSerialNumber;
    @Size(max = 64)
    @Column(name = "location")
    private String location;
    @Size(max = 64)
    @Column(name = "purchase_order")
    private String purchaseOrder;
    @Size(max = 16)
    @Column(name = "asm_position")
    private String asmPosition;
    @Size(max = 255)
    @Column(name = "asm_description")
    private String asmDescription;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<DeviceProperty> devicePropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<AlignmentRecord> alignmentRecordList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<InstallationRecord> installationRecordList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<DeviceArtifact> deviceArtifactList;
    @JoinColumn(name = "component_type", referencedColumnName = "component_type_id")
    @ManyToOne(optional = false)
    private ComponentType componentType;
    @OneToMany(mappedBy = "asmParent")
    private List<Device> deviceList;
    @JoinColumn(name = "asm_parent", referencedColumnName = "device_id")
    @ManyToOne
    private Device asmParent;

    public Device() {
    }

    public Device(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Device(Integer deviceId, String serialNumber, Date modifiedAt, String modifiedBy, int version) {
        this.deviceId = deviceId;
        this.serialNumber = serialNumber;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.version = version;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufModel() {
        return manufModel;
    }

    public void setManufModel(String manufModel) {
        this.manufModel = manufModel;
    }

    public String getManufSerialNumber() {
        return manufSerialNumber;
    }

    public void setManufSerialNumber(String manufSerialNumber) {
        this.manufSerialNumber = manufSerialNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getAsmPosition() {
        return asmPosition;
    }

    public void setAsmPosition(String asmPosition) {
        this.asmPosition = asmPosition;
    }

    public String getAsmDescription() {
        return asmDescription;
    }

    public void setAsmDescription(String asmDescription) {
        this.asmDescription = asmDescription;
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
    public List<DeviceProperty> getDevicePropertyList() {
        return devicePropertyList;
    }

    public void setDevicePropertyList(List<DeviceProperty> devicePropertyList) {
        this.devicePropertyList = devicePropertyList;
    }

    @XmlTransient
    public List<AlignmentRecord> getAlignmentRecordList() {
        return alignmentRecordList;
    }

    public void setAlignmentRecordList(List<AlignmentRecord> alignmentRecordList) {
        this.alignmentRecordList = alignmentRecordList;
    }

    @XmlTransient
    public List<InstallationRecord> getInstallationRecordList() {
        return installationRecordList;
    }

    public void setInstallationRecordList(List<InstallationRecord> installationRecordList) {
        this.installationRecordList = installationRecordList;
    }

    @XmlTransient
    public List<DeviceArtifact> getDeviceArtifactList() {
        return deviceArtifactList;
    }

    public void setDeviceArtifactList(List<DeviceArtifact> deviceArtifactList) {
        this.deviceArtifactList = deviceArtifactList;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    @XmlTransient
    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public Device getAsmParent() {
        return asmParent;
    }

    public void setAsmParent(Device asmParent) {
        this.asmParent = asmParent;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (deviceId != null ? deviceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Device)) {
            return false;
        }
        Device other = (Device) object;
        if ((this.deviceId == null && other.deviceId != null) || (this.deviceId != null && !this.deviceId.equals(other.deviceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.Device[ deviceId=" + deviceId + " ]";
    }
    
}
