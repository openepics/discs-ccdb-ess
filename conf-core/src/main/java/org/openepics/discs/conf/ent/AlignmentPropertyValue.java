package org.openepics.discs.conf.ent;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "alignment_property_values")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlignmentProperty.findAll", query = "SELECT a FROM AlignmentProperty a"),
    @NamedQuery(name = "AlignmentProperty.findByAlignPropId", query = "SELECT a FROM AlignmentProperty a WHERE a.id = :id"),
    @NamedQuery(name = "AlignmentProperty.findByInRepository", query = "SELECT a FROM AlignmentProperty a WHERE a.inRepository = :inRepository"),
    @NamedQuery(name = "AlignmentProperty.findByModifiedBy", query = "SELECT a FROM AlignmentProperty a WHERE a.modifiedBy = :modifiedBy")})
public class AlignmentPropertyValue extends PropertyValue {
    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "alignment_record")
    @ManyToOne(optional = false)
    private AlignmentRecord alignmentRecord;

    protected AlignmentPropertyValue() { }

    public AlignmentPropertyValue(boolean inRepository, String modifiedBy) {
        super(inRepository, modifiedBy);
    }

    public AlignmentRecord getAlignmentRecord() { return alignmentRecord; }
    public void setAlignmentRecord(AlignmentRecord alignmentRecord) { this.alignmentRecord = alignmentRecord; }

    @Override
    public String toString() {
        return "AlignmentProperty[ alignPropId=" + id + " ]";
    }
}
