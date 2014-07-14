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
@Table(name = "alignment_record")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlignmentRecord.findAll", query = "SELECT a FROM AlignmentRecord a"),
    @NamedQuery(name = "AlignmentRecord.findByAlignmentRecordId", query = "SELECT a FROM AlignmentRecord a WHERE a.alignmentRecordId = :alignmentRecordId"),
    @NamedQuery(name = "AlignmentRecord.findByRecordNumber", query = "SELECT a FROM AlignmentRecord a WHERE a.recordNumber = :recordNumber"),
    @NamedQuery(name = "AlignmentRecord.findByAlignmentDate", query = "SELECT a FROM AlignmentRecord a WHERE a.alignmentDate = :alignmentDate"),
    @NamedQuery(name = "AlignmentRecord.findByGlobalX", query = "SELECT a FROM AlignmentRecord a WHERE a.globalX = :globalX"),
    @NamedQuery(name = "AlignmentRecord.findByGlobalY", query = "SELECT a FROM AlignmentRecord a WHERE a.globalY = :globalY"),
    @NamedQuery(name = "AlignmentRecord.findByGlobalZ", query = "SELECT a FROM AlignmentRecord a WHERE a.globalZ = :globalZ"),
    @NamedQuery(name = "AlignmentRecord.findByGlobalPitch", query = "SELECT a FROM AlignmentRecord a WHERE a.globalPitch = :globalPitch"),
    @NamedQuery(name = "AlignmentRecord.findByGlobalYaw", query = "SELECT a FROM AlignmentRecord a WHERE a.globalYaw = :globalYaw"),
    @NamedQuery(name = "AlignmentRecord.findByGlobalRoll", query = "SELECT a FROM AlignmentRecord a WHERE a.globalRoll = :globalRoll"),
    @NamedQuery(name = "AlignmentRecord.findByModifiedAt", query = "SELECT a FROM AlignmentRecord a WHERE a.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "AlignmentRecord.findByModifiedBy", query = "SELECT a FROM AlignmentRecord a WHERE a.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "AlignmentRecord.findByVersion", query = "SELECT a FROM AlignmentRecord a WHERE a.version = :version")})
public class AlignmentRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "alignment_record_id")
    private Integer alignmentRecordId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "record_number")
    private String recordNumber;
    @Basic(optional = false)
    @NotNull
    @Column(name = "alignment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date alignmentDate;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "global_x")
    private Double globalX;
    @Column(name = "global_y")
    private Double globalY;
    @Column(name = "global_z")
    private Double globalZ;
    @Column(name = "global_pitch")
    private Double globalPitch;
    @Column(name = "global_yaw")
    private Double globalYaw;
    @Column(name = "global_roll")
    private Double globalRoll;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "alignmentRecord")
    private List<AlignmentArtifact> alignmentArtifactList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "alignmentRecord")
    private List<AlignmentProperty> alignmentPropertyList;
    @JoinColumn(name = "slot", referencedColumnName = "slot_id")
    @ManyToOne(optional = false)
    private Slot slot;
    @JoinColumn(name = "device", referencedColumnName = "device_id")
    @ManyToOne(optional = false)
    private Device device;

    protected AlignmentRecord() {
    }

    public AlignmentRecord(String recordNumber, Date alignmentDate, String modifiedBy) {
        this.recordNumber = recordNumber;
        this.alignmentDate = alignmentDate;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public Integer getAlignmentRecordId() {
        return alignmentRecordId;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
    }

    public Date getAlignmentDate() {
        return alignmentDate;
    }

    public void setAlignmentDate(Date alignmentDate) {
        this.alignmentDate = alignmentDate;
    }

    public Double getGlobalX() {
        return globalX;
    }

    public void setGlobalX(Double globalX) {
        this.globalX = globalX;
    }

    public Double getGlobalY() {
        return globalY;
    }

    public void setGlobalY(Double globalY) {
        this.globalY = globalY;
    }

    public Double getGlobalZ() {
        return globalZ;
    }

    public void setGlobalZ(Double globalZ) {
        this.globalZ = globalZ;
    }

    public Double getGlobalPitch() {
        return globalPitch;
    }

    public void setGlobalPitch(Double globalPitch) {
        this.globalPitch = globalPitch;
    }

    public Double getGlobalYaw() {
        return globalYaw;
    }

    public void setGlobalYaw(Double globalYaw) {
        this.globalYaw = globalYaw;
    }

    public Double getGlobalRoll() {
        return globalRoll;
    }

    public void setGlobalRoll(Double globalRoll) {
        this.globalRoll = globalRoll;
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
    public List<AlignmentArtifact> getAlignmentArtifactList() {
        return alignmentArtifactList;
    }

    public void setAlignmentArtifactList(List<AlignmentArtifact> alignmentArtifactList) {
        this.alignmentArtifactList = alignmentArtifactList;
    }

    @XmlTransient
    public List<AlignmentProperty> getAlignmentPropertyList() {
        return alignmentPropertyList;
    }

    public void setAlignmentPropertyList(List<AlignmentProperty> alignmentPropertyList) {
        this.alignmentPropertyList = alignmentPropertyList;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (alignmentRecordId != null ? alignmentRecordId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AlignmentRecord)) return false;

        AlignmentRecord other = (AlignmentRecord) object;
        if (this.alignmentRecordId == null && other.alignmentRecordId != null) return false;
        if (this.alignmentRecordId != null) return this.alignmentRecordId.equals(other.alignmentRecordId); // return true for same DB entity

        return this==object;
    }

    @Override
    public String toString() {
        return "AlignmentRecord[ alignmentRecordId=" + alignmentRecordId + " ]";
    }

}
