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
@Table(name = "slot_property_value")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotPropertyValue.findAll", query = "SELECT s FROM SlotPropertyValue s"),
    @NamedQuery(name = "SlotPropertyValue.findBySlotPropId", query = "SELECT s FROM SlotPropertyValue s "
            + "WHERE s.id = :id"),
    @NamedQuery(name = "SlotPropertyValue.findByInRepository", query = "SELECT s FROM SlotPropertyValue s "
            + "WHERE s.inRepository = :inRepository"),
    @NamedQuery(name = "SlotPropertyValue.findByModifiedBy", query = "SELECT s FROM SlotPropertyValue s "
            + "WHERE s.modifiedBy = :modifiedBy")
})
public class SlotPropertyValue extends PropertyValue {
    @JoinColumn(name = "slot")
    @ManyToOne(optional = false)
    private Slot slot;

    public SlotPropertyValue() { }

    public SlotPropertyValue(boolean inRepository) {
        super(inRepository);
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
