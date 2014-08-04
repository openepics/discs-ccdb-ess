package org.openepics.discs.conf.ent;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "alignment_artifact")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlignmentArtifact.findAll", query = "SELECT a FROM AlignmentArtifact a"),
    @NamedQuery(name = "AlignmentArtifact.findByArtifactId", query = "SELECT a FROM AlignmentArtifact a WHERE a.id = :id"),
    @NamedQuery(name = "AlignmentArtifact.findByName", query = "SELECT a FROM AlignmentArtifact a WHERE a.name = :name"),
    @NamedQuery(name = "AlignmentArtifact.findByIsInternal", query = "SELECT a FROM AlignmentArtifact a WHERE a.isInternal = :isInternal"),
    @NamedQuery(name = "AlignmentArtifact.findByModifiedBy", query = "SELECT a FROM AlignmentArtifact a WHERE a.modifiedBy = :modifiedBy")})
public class AlignmentArtifact extends ConfigurationEntity {
    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;

    @Basic(optional = false)
    @NotNull
    @Column(name = "is_internal")
    private boolean isInternal;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @NotNull
    @Column(name = "uri", columnDefinition="TEXT")
    private String uri;

    @JoinColumn(name = "alignment_record", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private AlignmentRecord alignmentRecord;

    protected AlignmentArtifact() {
    }

    public AlignmentArtifact(String name, boolean isInternal, String uri, String modifiedBy) {
        this.name = name;
        this.isInternal = isInternal;
        this.uri = uri;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsInternal() {
        return isInternal;
    }

    public void setIsInternal(boolean isInternal) {
        this.isInternal = isInternal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public AlignmentRecord getAlignmentRecord() {
        return alignmentRecord;
    }

    public void setAlignmentRecord(AlignmentRecord alignmentRecord) {
        this.alignmentRecord = alignmentRecord;
    }

    @Override
    public String toString() {
        return "AlignmentArtifact[ artifactId=" + id + " ]";
    }

}
