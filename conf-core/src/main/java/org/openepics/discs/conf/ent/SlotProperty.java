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
@Table(name = "slot_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotProperty.findAll", query = "SELECT s FROM SlotProperty s"),
    @NamedQuery(name = "SlotProperty.findBySlotPropId", query = "SELECT s FROM SlotProperty s WHERE s.id = :id"),
    @NamedQuery(name = "SlotProperty.findByInRepository", query = "SELECT s FROM SlotProperty s WHERE s.inRepository = :inRepository"),
    @NamedQuery(name = "SlotProperty.findByModifiedBy", query = "SELECT s FROM SlotProperty s WHERE s.modifiedBy = :modifiedBy")})
public class SlotProperty extends PropertyValue {
    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "slot")
    @ManyToOne(optional = false)
    private Slot slot;

    protected SlotProperty() {
    }

    public SlotProperty(boolean inRepository, String modifiedBy) {
        super(inRepository, modifiedBy);
    }

    public Slot getSlot() { return slot; }
    public void setSlot(Slot slot) { this.slot = slot; }

    @Override
    public String toString() {
        return "SlotProperty[ slotPropId=" + id + " ]";
    }
}
