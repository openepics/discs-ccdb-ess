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
@Table(name = "slot")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Slot.findAll", query = "SELECT s FROM Slot s"),
    @NamedQuery(name = "Slot.findBySlotId", query = "SELECT s FROM Slot s WHERE s.slotId = :slotId"),
    @NamedQuery(name = "Slot.findByName", query = "SELECT s FROM Slot s WHERE s.name = :name"),
    @NamedQuery(name = "Slot.findByDescription", query = "SELECT s FROM Slot s WHERE s.description = :description"),
    @NamedQuery(name = "Slot.findByIsAbstract", query = "SELECT s FROM Slot s WHERE s.isAbstract = :isAbstract"),
    @NamedQuery(name = "Slot.findByBeamlinePosition", query = "SELECT s FROM Slot s WHERE s.beamlinePosition = :beamlinePosition"),
    @NamedQuery(name = "Slot.findByGlobalX", query = "SELECT s FROM Slot s WHERE s.globalX = :globalX"),
    @NamedQuery(name = "Slot.findByGlobalY", query = "SELECT s FROM Slot s WHERE s.globalY = :globalY"),
    @NamedQuery(name = "Slot.findByGlobalZ", query = "SELECT s FROM Slot s WHERE s.globalZ = :globalZ"),
    @NamedQuery(name = "Slot.findByGlobalRoll", query = "SELECT s FROM Slot s WHERE s.globalRoll = :globalRoll"),
    @NamedQuery(name = "Slot.findByGlobalYaw", query = "SELECT s FROM Slot s WHERE s.globalYaw = :globalYaw"),
    @NamedQuery(name = "Slot.findByGlobalPitch", query = "SELECT s FROM Slot s WHERE s.globalPitch = :globalPitch"),
    @NamedQuery(name = "Slot.findByAsmComment", query = "SELECT s FROM Slot s WHERE s.asmComment = :asmComment"),
    @NamedQuery(name = "Slot.findByAsmPosition", query = "SELECT s FROM Slot s WHERE s.asmPosition = :asmPosition"),
    @NamedQuery(name = "Slot.findByComment", query = "SELECT s FROM Slot s WHERE s.comment = :comment"),
    @NamedQuery(name = "Slot.findByModifiedAt", query = "SELECT s FROM Slot s WHERE s.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "Slot.findByModifiedBy", query = "SELECT s FROM Slot s WHERE s.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "Slot.findByVersion", query = "SELECT s FROM Slot s WHERE s.version = :version")})
public class Slot implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "slot_id")
    private Integer slotId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_abstract")
    private boolean isAbstract;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "beamline_position")
    private Double beamlinePosition;
    @Column(name = "global_x")
    private Double globalX;
    @Column(name = "global_y")
    private Double globalY;
    @Column(name = "global_z")
    private Double globalZ;
    @Column(name = "global_roll")
    private Double globalRoll;
    @Column(name = "global_yaw")
    private Double globalYaw;
    @Column(name = "global_pitch")
    private Double globalPitch;
    @Size(max = 255)
    @Column(name = "asm_comment")
    private String asmComment;
    @Size(max = 16)
    @Column(name = "asm_position")
    private String asmPosition;
    @Size(max = 255)
    @Column(name = "comment")
    private String comment;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot")
    private List<AlignmentRecord> alignmentRecordList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot")
    private List<InstallationRecord> installationRecordList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot")
    private List<SlotLogRec> slotLogRecList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot")
    private List<SlotPair> slotPairList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot1")
    private List<SlotPair> slotPairList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot")
    private List<LsArtifact> lsArtifactList;
    @OneToMany(mappedBy = "asmSlot")
    private List<Slot> slotList;
    @JoinColumn(name = "asm_slot", referencedColumnName = "slot_id")
    @ManyToOne
    private Slot asmSlot;
    @JoinColumn(name = "component_type", referencedColumnName = "component_type_id")
    @ManyToOne(optional = false)
    private ComponentType componentType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot1")
    private List<SlotProperty> slotPropertyList;

    public Slot() {
    }

    public Slot(Integer slotId) {
        this.slotId = slotId;
    }

    public Slot(Integer slotId, String name, boolean isAbstract, Date modifiedAt, String modifiedBy, int version) {
        this.slotId = slotId;
        this.name = name;
        this.isAbstract = isAbstract;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.version = version;
    }

    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
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

    public boolean getIsAbstract() {
        return isAbstract;
    }

    public void setIsAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public Double getBeamlinePosition() {
        return beamlinePosition;
    }

    public void setBeamlinePosition(Double beamlinePosition) {
        this.beamlinePosition = beamlinePosition;
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

    public Double getGlobalRoll() {
        return globalRoll;
    }

    public void setGlobalRoll(Double globalRoll) {
        this.globalRoll = globalRoll;
    }

    public Double getGlobalYaw() {
        return globalYaw;
    }

    public void setGlobalYaw(Double globalYaw) {
        this.globalYaw = globalYaw;
    }

    public Double getGlobalPitch() {
        return globalPitch;
    }

    public void setGlobalPitch(Double globalPitch) {
        this.globalPitch = globalPitch;
    }

    public String getAsmComment() {
        return asmComment;
    }

    public void setAsmComment(String asmComment) {
        this.asmComment = asmComment;
    }

    public String getAsmPosition() {
        return asmPosition;
    }

    public void setAsmPosition(String asmPosition) {
        this.asmPosition = asmPosition;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
    public List<SlotLogRec> getSlotLogRecList() {
        return slotLogRecList;
    }

    public void setSlotLogRecList(List<SlotLogRec> slotLogRecList) {
        this.slotLogRecList = slotLogRecList;
    }

    @XmlTransient
    public List<SlotPair> getSlotPairList() {
        return slotPairList;
    }

    public void setSlotPairList(List<SlotPair> slotPairList) {
        this.slotPairList = slotPairList;
    }

    @XmlTransient
    public List<SlotPair> getSlotPairList1() {
        return slotPairList1;
    }

    public void setSlotPairList1(List<SlotPair> slotPairList1) {
        this.slotPairList1 = slotPairList1;
    }

    @XmlTransient
    public List<LsArtifact> getLsArtifactList() {
        return lsArtifactList;
    }

    public void setLsArtifactList(List<LsArtifact> lsArtifactList) {
        this.lsArtifactList = lsArtifactList;
    }

    @XmlTransient
    public List<Slot> getSlotList() {
        return slotList;
    }

    public void setSlotList(List<Slot> slotList) {
        this.slotList = slotList;
    }

    public Slot getAsmSlot() {
        return asmSlot;
    }

    public void setAsmSlot(Slot asmSlot) {
        this.asmSlot = asmSlot;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
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
        hash += (slotId != null ? slotId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Slot)) {
            return false;
        }
        Slot other = (Slot) object;
        if ((this.slotId == null && other.slotId != null) || (this.slotId != null && !this.slotId.equals(other.slotId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.Slot[ slotId=" + slotId + " ]";
    }
    
}
