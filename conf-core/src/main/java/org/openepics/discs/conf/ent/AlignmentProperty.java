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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "alignment_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlignmentProperty.findAll", query = "SELECT a FROM AlignmentProperty a"),
    @NamedQuery(name = "AlignmentProperty.findByAlignPropId", query = "SELECT a FROM AlignmentProperty a WHERE a.id = :id"),
    @NamedQuery(name = "AlignmentProperty.findByInRepository", query = "SELECT a FROM AlignmentProperty a WHERE a.inRepository = :inRepository"),
    @NamedQuery(name = "AlignmentProperty.findByModifiedAt", query = "SELECT a FROM AlignmentProperty a WHERE a.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "AlignmentProperty.findByModifiedBy", query = "SELECT a FROM AlignmentProperty a WHERE a.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "AlignmentProperty.findByVersion", query = "SELECT a FROM AlignmentProperty a WHERE a.version = :version")})
public class AlignmentProperty extends ConfigurationEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "prop_value", columnDefinition="TEXT")
    private String propValue;

    @Basic(optional = false)
    @NotNull
    @Column(name = "in_repository")
    private boolean inRepository;

    @JoinColumn(name = "alignment_record", referencedColumnName = "alignment_record_id")
    @ManyToOne(optional = false)
    private AlignmentRecord alignmentRecord;

    @JoinColumn(name = "property", referencedColumnName = "property_id")
    @ManyToOne(optional = false)
    private Property property;

    protected AlignmentProperty() {
    }

    public AlignmentProperty(boolean inRepository, String modifiedBy) {
        this.inRepository = inRepository;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    public boolean getInRepository() {
        return inRepository;
    }

    public void setInRepository(boolean inRepository) {
        this.inRepository = inRepository;
    }

    public AlignmentRecord getAlignmentRecord() {
        return alignmentRecord;
    }

    public void setAlignmentRecord(AlignmentRecord alignmentRecord) {
        this.alignmentRecord = alignmentRecord;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return "AlignmentProperty[ alignPropId=" + id + " ]";
    }

}
