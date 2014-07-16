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
@Table(name = "slot_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotProperty.findAll", query = "SELECT s FROM SlotProperty s"),
    @NamedQuery(name = "SlotProperty.findBySlotPropId", query = "SELECT s FROM SlotProperty s WHERE s.id = :id"),
    @NamedQuery(name = "SlotProperty.findByInRepository", query = "SELECT s FROM SlotProperty s WHERE s.inRepository = :inRepository"),
    @NamedQuery(name = "SlotProperty.findByModifiedAt", query = "SELECT s FROM SlotProperty s WHERE s.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "SlotProperty.findByModifiedBy", query = "SELECT s FROM SlotProperty s WHERE s.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "SlotProperty.findByVersion", query = "SELECT s FROM SlotProperty s WHERE s.version = :version")})
public class SlotProperty extends ConfigurationEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "prop_value", columnDefinition="TEXT")
    private String propValue;

    @Basic(optional = false)
    @NotNull
    @Column(name = "in_repository")
    private boolean inRepository;

    @JoinColumn(name = "unit", referencedColumnName = "id")
    @ManyToOne
    private Unit unit;

    @JoinColumn(name = "property", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Property property;

    @JoinColumn(name = "slot", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Slot slot;

    protected SlotProperty() {
    }

    public SlotProperty(boolean inRepository, String modifiedBy) {
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

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    @Override
    public String toString() {
        return "SlotProperty[ slotPropId=" + id + " ]";
    }

}
