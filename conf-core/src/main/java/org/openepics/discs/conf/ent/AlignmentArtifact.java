package org.openepics.discs.conf.ent;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An {@link Artifact} used in Alignment Records
 *
 * @author vuppala
 */
@Entity
@Table(name = "alignment_artifact", indexes = { @Index(columnList = "alignment_record") })
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlignmentArtifact.findAll", query = "SELECT a FROM AlignmentArtifact a"),
    @NamedQuery(name = "AlignmentArtifact.findByArtifactId", query = "SELECT a FROM AlignmentArtifact a "
            + "WHERE a.id = :id"),
    @NamedQuery(name = "AlignmentArtifact.findByName", query = "SELECT a FROM AlignmentArtifact a "
            + "WHERE a.name = :name"),
    @NamedQuery(name = "AlignmentArtifact.findByIsInternal", query = "SELECT a FROM AlignmentArtifact a "
            + "WHERE a.isInternal = :isInternal"),
    @NamedQuery(name = "AlignmentArtifact.findByModifiedBy", query = "SELECT a FROM AlignmentArtifact a "
            + "WHERE a.modifiedBy = :modifiedBy")
})
public class AlignmentArtifact extends Artifact {
    @JoinColumn(name = "alignment_record")
    @ManyToOne(optional = false)
    private AlignmentRecord alignmentRecord;

    protected AlignmentArtifact() { }

    public AlignmentArtifact(String name, boolean isInternal, String description, String uri) {
        super(name, isInternal, description, uri);
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
