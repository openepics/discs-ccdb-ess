package org.openepics.discs.conf.ent;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A {@link PropertyValue} used for {@link AlignmentRecord}s
 *
 * @author vuppala
 */
@Entity
@Table(name = "alignment_property_value")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlignmentPropertyValue.findAll", query = "SELECT a FROM AlignmentPropertyValue a"),
    @NamedQuery(name = "AlignmentPropertyValue.findByAlignPropId", query = "SELECT a FROM AlignmentPropertyValue a "
            + "WHERE a.id = :id"),
    @NamedQuery(name = "AlignmentPropertyValue.findByInRepository", query = "SELECT a FROM AlignmentPropertyValue a "
            + "WHERE a.inRepository = :inRepository"),
    @NamedQuery(name = "AlignmentPropertyValue.findByModifiedBy", query = "SELECT a FROM AlignmentPropertyValue a "
            + "WHERE a.modifiedBy = :modifiedBy")
})
public class AlignmentPropertyValue extends PropertyValue {
    @JoinColumn(name = "alignment_record")
    @ManyToOne(optional = false)
    private AlignmentRecord alignmentRecord;

    public AlignmentPropertyValue() { }

    public AlignmentPropertyValue(boolean inRepository) {
        super(inRepository);
    }

    public AlignmentRecord getAlignmentRecord() {
        return alignmentRecord;
    }
    public void setAlignmentRecord(AlignmentRecord alignmentRecord) {
        this.alignmentRecord = alignmentRecord;
    }

    @Override
    public String toString() {
        return "AlignmentProperty[ alignPropId=" + id + " ]";
    }
}
