package org.openepics.discs.conf.ent;

import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "alignment_record")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlignmentRecord.findAll", query = "SELECT a FROM AlignmentRecord a"),
    @NamedQuery(name = "AlignmentRecord.findByAlignmentRecordId", query = "SELECT a FROM AlignmentRecord a WHERE a.id = :id"),
    @NamedQuery(name = "AlignmentRecord.findByRecordNumber", query = "SELECT a FROM AlignmentRecord a WHERE a.recordNumber = :recordNumber"),
    @NamedQuery(name = "AlignmentRecord.findByAlignmentDate", query = "SELECT a FROM AlignmentRecord a WHERE a.alignmentDate = :alignmentDate"),
    @NamedQuery(name = "AlignmentRecord.findByModifiedBy", query = "SELECT a FROM AlignmentRecord a WHERE a.modifiedBy = :modifiedBy")})
public class AlignmentRecord extends ConfigurationEntity {
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

    @Embedded
    private AlignmentInformation alignmentInfo;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "alignmentRecord")
    private List<AlignmentArtifact> alignmentArtifactList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "alignmentRecord")
    private List<AlignmentPropertyValue> alignmentPropertyList;

    @JoinColumn(name = "slot")
    @ManyToOne(optional = false)
    private Slot slot;

    @JoinColumn(name = "device")
    @ManyToOne(optional = false)
    private Device device;

    @ManyToMany
    @JoinTable(name = "alignment_tags",
        joinColumns = { @JoinColumn(name = "alignment_id") }, inverseJoinColumns = { @JoinColumn(name = "tag_id") })
    private Set<Tag> tags;

    protected AlignmentRecord() {
    }

    public AlignmentRecord(String recordNumber, Date alignmentDate) {
        this.alignmentInfo = new AlignmentInformation();
        this.recordNumber = recordNumber;
        this.alignmentDate = alignmentDate;
    }

    public String getRecordNumber() { return recordNumber; }
    public void setRecordNumber(String recordNumber) { this.recordNumber = recordNumber; }

    public Date getAlignmentDate() { return alignmentDate; }
    public void setAlignmentDate(Date alignmentDate) { this.alignmentDate = alignmentDate; }

    public AlignmentInformation getAlignmentInformation() { 
        if (alignmentInfo == null) {
            alignmentInfo = new AlignmentInformation();
        }
        return alignmentInfo; 
    }

    @XmlTransient
    @JsonIgnore
    public List<AlignmentArtifact> getAlignmentArtifactList() { return alignmentArtifactList; }

    @XmlTransient
    @JsonIgnore
    public List<AlignmentPropertyValue> getAlignmentPropertyList() { return alignmentPropertyList; }

    public Slot getSlot() { return slot; }
    public void setSlot(Slot slot) { this.slot = slot; }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    @XmlTransient
    @JsonIgnore
    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }

    @Override
    public String toString() { return "AlignmentRecord[ alignmentRecordId=" + id + " ]"; }

}
