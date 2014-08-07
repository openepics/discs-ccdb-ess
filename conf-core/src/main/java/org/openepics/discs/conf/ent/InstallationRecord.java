package org.openepics.discs.conf.ent;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "installation_record")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InstallationRecord.findAll", query = "SELECT i FROM InstallationRecord i"),
    @NamedQuery(name = "InstallationRecord.findByInstallationRecordId", query = "SELECT i FROM InstallationRecord i WHERE i.id = :id"),
    @NamedQuery(name = "InstallationRecord.findByInstallDate", query = "SELECT i FROM InstallationRecord i WHERE i.installDate = :installDate"),
    @NamedQuery(name = "InstallationRecord.findByUninstallDate", query = "SELECT i FROM InstallationRecord i WHERE i.uninstallDate = :uninstallDate"),
    @NamedQuery(name = "InstallationRecord.findByModifiedBy", query = "SELECT i FROM InstallationRecord i WHERE i.modifiedBy = :modifiedBy")})
public class InstallationRecord extends ConfigurationEntity {
    private static final long serialVersionUID = 1L;

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

    @JoinColumn(name = "slot")
    @ManyToOne(optional = false)
    private Slot slot;

    @JoinColumn(name = "device")
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

    public String getRecordNumber() { return recordNumber; }
    public void setRecordNumber(String recordNumber) { this.recordNumber = recordNumber; }

    public Date getInstallDate() { return installDate; }
    public void setInstallDate(Date installDate) { this.installDate = installDate; }

    public Date getUninstallDate() { return uninstallDate; }
    public void setUninstallDate(Date uninstallDate) { this.uninstallDate = uninstallDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Slot getSlot() { return slot; }
    public void setSlot(Slot slot) { this.slot = slot; }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    @XmlTransient
    public List<InstallationArtifact> getInstallationArtifactList() { return installationArtifactList; }

    @Override
    public String toString() { return "InstallationRecord[ installationRecordId=" + id + " ]"; }
}
