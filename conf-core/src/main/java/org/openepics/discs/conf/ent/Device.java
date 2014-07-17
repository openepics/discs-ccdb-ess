package org.openepics.discs.conf.ent;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "device")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Device.findAll", query = "SELECT d FROM Device d"),
    @NamedQuery(name = "Device.findByDeviceId", query = "SELECT d FROM Device d WHERE d.id = :id"),
    @NamedQuery(name = "Device.findBySerialNumber", query = "SELECT d FROM Device d WHERE d.serialNumber = :serialNumber"),
    @NamedQuery(name = "Device.findByStatus", query = "SELECT d FROM Device d WHERE d.status = :status"),
    @NamedQuery(name = "Device.findByManufacturer", query = "SELECT d FROM Device d WHERE d.manufacturer = :manufacturer"),
    @NamedQuery(name = "Device.findByManufModel", query = "SELECT d FROM Device d WHERE d.manufModel = :manufModel"),
    @NamedQuery(name = "Device.findByManufSerialNumber", query = "SELECT d FROM Device d WHERE d.manufSerialNumber = :manufSerialNumber"),
    @NamedQuery(name = "Device.findByLocation", query = "SELECT d FROM Device d WHERE d.location = :location"),
    @NamedQuery(name = "Device.findByPurchaseOrder", query = "SELECT d FROM Device d WHERE d.purchaseOrder = :purchaseOrder"),
    @NamedQuery(name = "Device.findByAsmPosition", query = "SELECT d FROM Device d WHERE d.asmPosition = :asmPosition"),
    @NamedQuery(name = "Device.findByAsmDescription", query = "SELECT d FROM Device d WHERE d.asmDescription = :asmDescription"),
    @NamedQuery(name = "Device.findByModifiedBy", query = "SELECT d FROM Device d WHERE d.modifiedBy = :modifiedBy")})
public class Device extends ConfigurationEntity {
    private static final long serialVersionUID = 1L;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<DeviceProperty> devicePropertyList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<AlignmentRecord> alignmentRecordList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<InstallationRecord> installationRecordList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "device")
    private List<DeviceArtifact> deviceArtifactList;

    @JoinColumn(name = "component_type", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentType componentType;

    @OneToMany(mappedBy = "asmParent")
    private List<Device> deviceList;

    @JoinColumn(name = "asm_parent", referencedColumnName = "id")
    @ManyToOne
    private Device asmParent;

    @ManyToMany
    @JoinTable(name = "device_tags",
        joinColumns = { @JoinColumn(name = "device_id") }, inverseJoinColumns = { @JoinColumn(name = "tag_id") })
    private Set<Tag> tags;

    protected Device() {
    }

    public Device(String serialNumber, String modifiedBy) {
        this.serialNumber = serialNumber;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Character getStatus() { return status; }
    public void setStatus(Character status) { this.status = status; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getManufacturerModel() { return manufModel; }
    public void setManufacturerModel(String manufModel) { this.manufModel = manufModel; }

    public String getManufacturerSerialNumber() { return manufSerialNumber; }
    public void setManufacturerSerialNumber(String manufSerialNumber) { this.manufSerialNumber = manufSerialNumber; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(String purchaseOrder) { this.purchaseOrder = purchaseOrder; }

    public String getAssemblyPosition() { return asmPosition; }
    public void setAssemblyPosition(String asmPosition) { this.asmPosition = asmPosition; }

    public String getAssemblyDescription() { return asmDescription; }
    public void setAssemblyDescription(String asmDescription) { this.asmDescription = asmDescription; }

    @XmlTransient
    public List<DeviceProperty> getDevicePropertyList() { return devicePropertyList; }
    public void setDevicePropertyList(List<DeviceProperty> devicePropertyList) { this.devicePropertyList = devicePropertyList; }

    @XmlTransient
    public List<AlignmentRecord> getAlignmentRecordList() { return alignmentRecordList; }
    public void setAlignmentRecordList(List<AlignmentRecord> alignmentRecordList) { this.alignmentRecordList = alignmentRecordList; }

    @XmlTransient
    public List<InstallationRecord> getInstallationRecordList() { return installationRecordList; }
    public void setInstallationRecordList(List<InstallationRecord> installationRecordList) { this.installationRecordList = installationRecordList; }

    @XmlTransient
    public List<DeviceArtifact> getDeviceArtifactList() { return deviceArtifactList; }
    public void setDeviceArtifactList(List<DeviceArtifact> deviceArtifactList) { this.deviceArtifactList = deviceArtifactList; }

    public ComponentType getComponentType() { return componentType; }
    public void setComponentType(ComponentType componentType) { this.componentType = componentType; }

    @XmlTransient
    public List<Device> getDeviceList() { return deviceList; }
    public void setDeviceList(List<Device> deviceList) { this.deviceList = deviceList; }

    public Device getAssemblyParent() { return asmParent; }
    public void setAssemblyParent(Device asmParent) { this.asmParent = asmParent; }

    @XmlTransient
    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }

    @Override
    public String toString() { return "Device[ deviceId=" + id + " ]"; }

}
