package org.openepics.discs.conf.ent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "slot")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Slot.findAll", query = "SELECT s FROM Slot s"),
    @NamedQuery(name = "Slot.findBySlotId", query = "SELECT s FROM Slot s WHERE s.id = :id"),
    @NamedQuery(name = "Slot.findByName", query = "SELECT s FROM Slot s WHERE s.name = :name"),
    @NamedQuery(name = "Slot.findByNameContaining", query = "SELECT s FROM Slot s WHERE s.name LIKE :name"),
    @NamedQuery(name = "Slot.findByIsHostingSlot", query = "SELECT s FROM Slot s WHERE s.isHostingSlot = :isHostingSlot"),
    @NamedQuery(name = "Slot.findByBeamlinePosition", query = "SELECT s FROM Slot s WHERE s.beamlinePosition = :beamlinePosition"),
    @NamedQuery(name = "Slot.findByModifiedBy", query = "SELECT s FROM Slot s WHERE s.modifiedBy = :modifiedBy")})
public class Slot extends ConfigurationEntity {
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
    @Column(name = "is_hosting_slot")
    private boolean isHostingSlot;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "beamline_position")
    private Double beamlinePosition;

    @Embedded
    private AlignmentInformation positionInfo;

    @Size(max = 255)
    @Column(name = "asm_comment")
    private String asmComment;

    @Size(max = 16)
    @Column(name = "asm_position")
    private String asmPosition;

    @Size(max = 255)
    @Column(name = "comment")
    private String comment;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot")
    private List<SlotArtifact> slotArtifactList;

    @JoinColumn(name = "component_type")
    @ManyToOne(optional = false)
    private ComponentType componentType;

    @OneToMany(mappedBy = "asmSlot")
    private List<Slot> slotList;

    @JoinColumn(name = "asm_slot")
    @ManyToOne
    private Slot asmSlot;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot")
    private List<AlignmentRecord> alignmentRecordList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot")
    private List<InstallationRecord> installationRecordList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "childSlot")
    private List<SlotPair> childrenSlots;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentSlot")
    private List<SlotPair> parentSlots;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slot")
    private List<SlotPropertyValue> slotPropertyList;

    @ManyToMany
    @JoinTable(name = "slot_tags",
        joinColumns = { @JoinColumn(name = "slot_id") }, inverseJoinColumns = { @JoinColumn(name = "tag_id") })
    private Set<Tag> tags;


    protected Slot() {
    }

    public Slot(String name, boolean isHostingSlot) {
        this.positionInfo = new AlignmentInformation();
        this.name = name;
        this.isHostingSlot = isHostingSlot;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description;    }
    public void setDescription(String description) { this.description = description; }

    public boolean getIsHostingSlot() { return isHostingSlot; }
    public void setIsHostingSlot(boolean isHostingSlot) { this.isHostingSlot = isHostingSlot; }

    public Double getBeamlinePosition() { return beamlinePosition; }
    public void setBeamlinePosition(Double beamlinePosition) { this.beamlinePosition = beamlinePosition; }

    public AlignmentInformation getPositionInformation() { return positionInfo; }

    public String getAssemblyComment() { return asmComment; }
    public void setAssemblyComment(String asmComment) { this.asmComment = asmComment; }

    public String getAssemblyPosition() { return asmPosition; }
    public void setAssemblyPosition(String asmPosition) { this.asmPosition = asmPosition; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    @XmlTransient
    @JsonIgnore
    public List<SlotArtifact> getSlotArtifactList() {
        if (slotArtifactList == null) {
            slotArtifactList = new ArrayList<>();
        }
        return slotArtifactList;
    }

    public ComponentType getComponentType() { return componentType; }
    public void setComponentType(ComponentType componentType) { this.componentType = componentType; }

    @XmlTransient
    @JsonIgnore
    public List<Slot> getSlotList() { return slotList; }

    public Slot getAssemblySlot() { return asmSlot; }
    public void setAssemblySlot(Slot asmSlot) { this.asmSlot = asmSlot; }

    @XmlTransient
    @JsonIgnore
    public List<AlignmentRecord> getAlignmentRecordList() { return alignmentRecordList; }

    @XmlTransient
    @JsonIgnore
    public List<InstallationRecord> getInstallationRecordList() {
        if (installationRecordList == null) {
            installationRecordList = new ArrayList<>();
        }
        return installationRecordList;
    }

    @XmlTransient
    @JsonIgnore
    public List<SlotPair> getChildrenSlotsPairList() {
        if (childrenSlots == null) {
            childrenSlots = new ArrayList<>();
        }
        return childrenSlots;
    }

    @XmlTransient
    @JsonIgnore
    public List<SlotPair> getParentSlotsPairList() {
        if (parentSlots == null) {
            parentSlots = new ArrayList<>();
        }
        return parentSlots;
    }

    @XmlTransient
    @JsonIgnore
    public List<SlotPropertyValue> getSlotPropertyList() {
        if (slotPropertyList == null) {
            slotPropertyList = new ArrayList<>();
        }
        return slotPropertyList;
    }

    @XmlTransient
    @JsonIgnore
    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }

    @Override
    public String toString() { return "Slot[ slotId=" + id + " ]"; }
}
