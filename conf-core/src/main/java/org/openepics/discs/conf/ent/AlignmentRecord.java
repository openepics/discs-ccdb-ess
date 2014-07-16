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
@Table(name = "alignment_record")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlignmentRecord.findAll", query = "SELECT a FROM AlignmentRecord a"),
    @NamedQuery(name = "AlignmentRecord.findByAlignmentRecordId", query = "SELECT a FROM AlignmentRecord a WHERE a.id = :id"),
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
public class AlignmentRecord extends ConfigurationEntity {
    private static final long serialVersionUID = 1L;

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
    public String toString() {
        return "AlignmentRecord[ alignmentRecordId=" + id + " ]";
    }

}
