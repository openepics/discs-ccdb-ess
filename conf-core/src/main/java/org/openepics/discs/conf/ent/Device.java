package org.openepics.discs.conf.ent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "device", indexes = { @Index(columnList = "serial_number"), @Index(columnList = "component_type") })
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Device.findAll", query = "SELECT d FROM Device d"),
    // device instance does not have a name. This named query is introduced to satisfy the ReadOnlyDAO assumption.
    @NamedQuery(name = "Device.findByName", query = "SELECT d FROM Device d WHERE d.serialNumber = :name"),
    @NamedQuery(name = "Device.findByComponentType", query = "SELECT d FROM Device d "
            + "WHERE d.componentType = :componentType"),
    @NamedQuery(name = "Device.uninstalledDevicesByType", query = "SELECT d from Device d "
            + "WHERE d.componentType = :componentType "
            + "AND (NOT EXISTS (SELECT ir FROM InstallationRecord ir WHERE d = ir.device) "
            + "OR NOT EXISTS (SELECT ir FROM InstallationRecord ir WHERE d = ir.device AND ir.uninstallDate IS NULL))")
})
public class Device extends ConfigurationEntity {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "serial_number", unique = true)
    private String serialNumber;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeviceStatus status = DeviceStatus.DEFINED;

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

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "manuf_model")
    private String manufModel;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<DevicePropertyValue> devicePropertyList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<AlignmentRecord> alignmentRecordList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<InstallationRecord> installationRecordList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<DeviceArtifact> deviceArtifactList = new ArrayList<>();

    @JoinColumn(name = "component_type")
    @ManyToOne(optional = false)
    private ComponentType componentType;

    @OneToMany(mappedBy = "asmParent")
    private List<Device> deviceList = new ArrayList<>();

    @JoinColumn(name = "asm_parent")
    @ManyToOne
    private Device asmParent;

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "device_tag",
               joinColumns = { @JoinColumn(name = "device_id") }, inverseJoinColumns = { @JoinColumn(name = "tag_id") })
    private Set<Tag> tags = new HashSet<>();

    protected Device() {
    }

    public Device(String serialNumber) {
        this.serialNumber = serialNumber;
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

    public DeviceStatus getStatus() {
        return status;
    }
    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    public String getManufacturerSerialNumber() {
        return manufSerialNumber;
    }
    public void setManufacturerSerialNumber(String manufSerialNumber) {
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

    public String getAssemblyPosition() {
        return asmPosition;
    }
    public void setAssemblyPosition(String asmPosition) {
        this.asmPosition = asmPosition;
    }

    public String getAssemblyDescription() {
        return asmDescription;
    }
    public void setAssemblyDescription(String asmDescription) {
        this.asmDescription = asmDescription;
    }

    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturerModel() {
        return manufModel;
    }
    public void setManufacturerModel(String manufModel) {
        this.manufModel = manufModel;
    }

    @XmlTransient
    @JsonIgnore
    public List<DevicePropertyValue> getDevicePropertyList() {
        return devicePropertyList;
    }

    @XmlTransient
    @JsonIgnore
    public List<AlignmentRecord> getAlignmentRecordList() {
        return alignmentRecordList;
    }

    @XmlTransient
    @JsonIgnore
    public List<InstallationRecord> getInstallationRecordList() {
        return installationRecordList;
    }

    @XmlTransient
    @JsonIgnore
    public List<DeviceArtifact> getDeviceArtifactList() {
        return deviceArtifactList;
    }

    public ComponentType getComponentType() {
        return componentType;
    }
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    @XmlTransient
    @JsonIgnore
    public List<Device> getDeviceList() {
        return deviceList;
    }

    public Device getAssemblyParent() {
        return asmParent;
    }
    public void setAssemblyParent(Device asmParent) {
        this.asmParent = asmParent;
    }

    @XmlTransient
    @JsonIgnore
    public Set<Tag> getTags() {
        return tags;
    }
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Device[ deviceId=" + id + " ]";
    }

}
