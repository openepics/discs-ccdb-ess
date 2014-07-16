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
@Table(name = "installation_record")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InstallationRecord.findAll", query = "SELECT i FROM InstallationRecord i"),
    @NamedQuery(name = "InstallationRecord.findByInstallationRecordId", query = "SELECT i FROM InstallationRecord i WHERE i.installationRecordId = :installationRecordId"),
    @NamedQuery(name = "InstallationRecord.findByRecordNumber", query = "SELECT i FROM InstallationRecord i WHERE i.recordNumber = :recordNumber"),
    @NamedQuery(name = "InstallationRecord.findByInstallDate", query = "SELECT i FROM InstallationRecord i WHERE i.installDate = :installDate"),
    @NamedQuery(name = "InstallationRecord.findByUninstallDate", query = "SELECT i FROM InstallationRecord i WHERE i.uninstallDate = :uninstallDate"),
    @NamedQuery(name = "InstallationRecord.findByModifiedAt", query = "SELECT i FROM InstallationRecord i WHERE i.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "InstallationRecord.findByModifiedBy", query = "SELECT i FROM InstallationRecord i WHERE i.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "InstallationRecord.findByVersion", query = "SELECT i FROM InstallationRecord i WHERE i.version = :version")})
public class InstallationRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "installation_record_id")
    private Integer installationRecordId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "record_number")
    private String recordNumber;

    @Basic(optional = false)
    @NotNull
    @Column(name = "install_date")
    @Temporal(TemporalType.DATE)
    private Date installDate;

    @Column(name = "uninstall_date")
    @Temporal(TemporalType.DATE)
    private Date uninstallDate;

    @Column(name = "notes", columnDefinition="TEXT")
    private String notes;

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

    @JoinColumn(name = "slot", referencedColumnName = "slot_id")
    @ManyToOne(optional = false)
    private Slot slot;

    @JoinColumn(name = "device", referencedColumnName = "device_id")
    @ManyToOne(optional = false)
    private Device device;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "installationRecord")
    private List<InstallationArtifact> installationArtifactList;

    protected InstallationRecord() {
    }

    public InstallationRecord(String recordNumber, Date installDate, String modifiedBy) {
        this.recordNumber = recordNumber;
        this.installDate = installDate;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public Integer getInstallationRecordId() {
        return installationRecordId;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
    }

    public Date getInstallDate() {
        return installDate;
    }

    public void setInstallDate(Date installDate) {
        this.installDate = installDate;
    }

    public Date getUninstallDate() {
        return uninstallDate;
    }

    public void setUninstallDate(Date uninstallDate) {
        this.uninstallDate = uninstallDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @XmlTransient
    public List<InstallationArtifact> getInstallationArtifactList() {
        return installationArtifactList;
    }

    public void setInstallationArtifactList(List<InstallationArtifact> installationArtifactList) {
        this.installationArtifactList = installationArtifactList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (installationRecordId != null ? installationRecordId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof InstallationRecord)) return false;

        InstallationRecord other = (InstallationRecord) object;
        if (this.installationRecordId == null && other.installationRecordId != null) return false;
        if (this.installationRecordId != null) return this.installationRecordId.equals(other.installationRecordId); // return true for same DB entity

        return this==object;
    }

    @Override
    public String toString() {
        return "InstallationRecord[ installationRecordId=" + installationRecordId + " ]";
    }

}
